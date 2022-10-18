package com.ruoyi.common.core.web.controller;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.core.constant.HttpStatus;
import com.ruoyi.common.core.utils.DateUtils;
import com.ruoyi.common.core.utils.StringUtils;
import com.ruoyi.common.core.utils.sql.SqlUtil;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.common.core.web.handle.QueryCondition;
import com.ruoyi.common.core.web.handle.QueryGroup;
import com.ruoyi.common.core.web.handle.QueryRule;
import com.ruoyi.common.core.web.page.PageDomain;
import com.ruoyi.common.core.web.page.TableDataInfo;
import com.ruoyi.common.core.web.page.TableSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.beans.PropertyEditorSupport;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * web层通用数据处理
 * 
 * @author ruoyi
 */
public class BaseController
{
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * 将前台传递过来的日期格式的字符串，自动转化为Date类型
     */
    @InitBinder
    public void initBinder(WebDataBinder binder)
    {
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport()
        {
            @Override
            public void setAsText(String text)
            {
                setValue(DateUtils.parseDate(text));
            }
        });
    }

    /**
     * 设置请求分页数据
     */
    protected void startPage()
    {
        PageDomain pageDomain = TableSupport.buildPageRequest();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        if (StringUtils.isNotNull(pageNum) && StringUtils.isNotNull(pageSize))
        {
            String orderBy = SqlUtil.escapeOrderBySql(pageDomain.getOrderBy());
            Boolean reasonable = pageDomain.getReasonable();
            PageHelper.startPage(pageNum, pageSize, orderBy).setReasonable(reasonable);
        }
    }

    /**
     * 响应请求分页数据
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected TableDataInfo getDataTable(List<?> list)
    {
        TableDataInfo rspData = new TableDataInfo();
        rspData.setCode(HttpStatus.SUCCESS);
        rspData.setRows(list);
        rspData.setMsg("查询成功");
        rspData.setTotal(new PageInfo(list).getTotal());
        return rspData;
    }
    protected  <T> QueryWrapper<T> getWrapper(QueryCondition<T> queryCondition) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        if (ObjectUtil.isEmpty(queryCondition)){
            return wrapper;
        }
        QueryGroup[] groups = queryCondition.getGroups();
        if (groups!=null){
            for (QueryGroup group : Arrays.stream(groups).filter(f-> ObjectUtil.isNotEmpty(f.getRules())).collect(Collectors.toList())) {
                wrapper.and( d -> parseWhere(d,group.getRules(),"or".equalsIgnoreCase(group.getOp())) );
            }
        }
        QueryRule[] where = queryCondition.getWhere();
        parseWhere(wrapper, where,false);
        handleSort(queryCondition.getOrderBy(),queryCondition.getOrderType(),wrapper);
        return  wrapper;
    }

    private  <T> QueryWrapper<T> parseWhere(QueryWrapper<T> wrapper, QueryRule[] rules, boolean isGroupOr) {
        parseRules(rules, wrapper,isGroupOr);
        return wrapper;
    }


    private  <T> void parseRules(QueryRule[] rules, QueryWrapper<T> wrapper, boolean isGroupOr) {
        if (rules !=null && rules.length > 0){

            for (int i = 0; i < rules.length; i++) {
                QueryRule queryRule = rules[i];
                if ("equals".equalsIgnoreCase(queryRule.getOp()) || "eq".equalsIgnoreCase(queryRule.getOp())) {
                    wrapper.eq(getFieldName(queryRule.getField()), queryRule.getValue());
                } else if ("like".equalsIgnoreCase(queryRule.getOp())) {
                    wrapper.like(getFieldName(queryRule.getField()), queryRule.getValue());
                } else if ("likeLeft".equalsIgnoreCase(queryRule.getOp())) {
                    wrapper.likeLeft(getFieldName(queryRule.getField()), queryRule.getValue());
                }else if ("likeRight".equalsIgnoreCase(queryRule.getOp())) {
                    wrapper.likeRight(getFieldName(queryRule.getField()), queryRule.getValue());
                }else if ("between".equalsIgnoreCase(queryRule.getOp())) {
                    List<Object> value = getValueList(queryRule, "between 入参要求是数组/List");
                    if (value == null || value.size() != 2) {
                        throw new RuntimeException("between 参数个数不等于2");
                    }
                    // Array.get(value, 1)
                    wrapper.between(getFieldName(queryRule.getField()), value.get(0),value.get(1));
                }else if ("in".equalsIgnoreCase(queryRule.getOp())){
                    List<Object> value = getValueList(queryRule, "in 入参要求是数组/List");
                    wrapper.in(getFieldName(queryRule.getField()), value.toArray());
                }else if ("notIn".equalsIgnoreCase(queryRule.getOp())){
                    List<Object> value = getValueList(queryRule, "notIn 入参要求是数组/List");
                    wrapper.notIn(getFieldName(queryRule.getField()), value.toArray());
                }else if ("gt".equalsIgnoreCase(queryRule.getOp())){
                    wrapper.gt(getFieldName(queryRule.getField()),queryRule.getValue());
                }else if ("lt".equalsIgnoreCase(queryRule.getOp())){
                    wrapper.lt(getFieldName(queryRule.getField()),queryRule.getValue());
                }else if ("ge".equalsIgnoreCase(queryRule.getOp())){
                    wrapper.ge(getFieldName(queryRule.getField()),queryRule.getValue());
                }else if ("le".equalsIgnoreCase(queryRule.getOp())){
                    wrapper.le(getFieldName(queryRule.getField()),queryRule.getValue());
                }else if ("ne".equalsIgnoreCase(queryRule.getOp())){
                    wrapper.ne(getFieldName(queryRule.getField()),queryRule.getValue());
                }else {
                    throw new RuntimeException("sql查询条件类型未支持");
                }
                // use in group rules's 'or' connection
                if (isGroupOr && i < rules.length-1) wrapper.or();
            }
        }
    }

    private List<Object> getValueList(QueryRule queryRule, String s) {
        List<Object> value = null;
        if (queryRule.getValue() instanceof Collection) {
            value = (List) queryRule.getValue();
        } else if (queryRule.getValue().getClass().isArray()) {
            value = Arrays.asList(queryRule.getValue());
        } else {
            throw new RuntimeException(s);
        }
        return value;
    }

    public static final String  START = "${";
    public static final String  END  = "}";

    public String getFieldName(String rawFieldName){
        String fieldName = rawFieldName;
        if (StringUtils.contains(rawFieldName,START) && StringUtils.contains(rawFieldName,END)){
            String subField = StrUtil.subBetween(rawFieldName, START, END);
            fieldName = StrUtil.replaceIgnoreCase(rawFieldName, START+subField+END, StrUtil.toUnderlineCase(subField));
            //递归解析
            if (StringUtils.contains(fieldName,START) && StringUtils.contains(fieldName,END)) {
                fieldName = getFieldName(fieldName);
            }
        }else {
            fieldName = StrUtil.toUnderlineCase(fieldName);
        }
        return fieldName;
    }

    public static final String[] ODER_TYPE = {"asc","desc","ASC","DESC"};

    private <T> void handleSort(String[] orderBy, String[] orderType, QueryWrapper<T> queryWrapper){
        if (orderBy!=null && orderType!=null ){
            if (orderBy.length != orderType.length){
                throw new RuntimeException("动态查询排序入参有误");
            }
            if (!Arrays.asList(ODER_TYPE).containsAll(Arrays.stream(orderType).distinct().collect(Collectors.toList()))){
                throw new RuntimeException("排序只支持asc,desc");
            }
            for (int i = 0; i < orderBy.length; i++) {
                String by = orderBy[i];
                String type = orderType[i];
                if ("asc".equalsIgnoreCase(type)){
                    queryWrapper.orderByAsc(getFieldName(by));
                }else if("desc".equalsIgnoreCase(type)){
                    queryWrapper.orderByDesc(getFieldName(by));
                }
            }
        }

    }

    /**
     * 响应返回结果
     * 
     * @param rows 影响行数
     * @return 操作结果
     */
    protected AjaxResult toAjax(int rows)
    {
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 响应返回结果
     * 
     * @param result 结果
     * @return 操作结果
     */
    protected AjaxResult toAjax(boolean result)
    {
        return result ? success() : error();
    }

    /**
     * 返回成功
     */
    public AjaxResult success()
    {
        return AjaxResult.success();
    }

    /**
     * 返回失败消息
     */
    public AjaxResult error()
    {
        return AjaxResult.error();
    }

    /**
     * 返回成功消息
     */
    public AjaxResult success(String message)
    {
        return AjaxResult.success(message);
    }

    /**
     * 返回失败消息
     */
    public AjaxResult error(String message)
    {
        return AjaxResult.error(message);
    }
}
