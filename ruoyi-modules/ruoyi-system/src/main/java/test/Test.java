package test;



import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.system.api.domain.SysUser;


import java.lang.reflect.Array;

public class Test {
   /* public static void main2(String[] args) {
        JSONObject req = new JSONObject();
        req.put("unloadDetailId","8A3F253D9AE448F79C8E640BA0F54869");
        // 本机 是http 和 https 都能使用，但是公司电脑为什么 https不能用呢
        String result = HttpRequest.post("https://epwms-cs.euroports.com.cn/M/QrCode/GetUnloadInfo")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .body(JSONUtil.toJsonStr(req))
                .execute().body();
        System.out.println(" result " +result);
    }
*/

    public static void main(String[] args) {
        QueryGroup queryGroup = new QueryGroup(new QueryRule[]{new QueryRule("name", "like", "abyss"), new QueryRule("age", "between", new Object[]{"10","25"})}, "or");

        QueryRule[] queryRules = {new QueryRule("height", "equals", "175"), new QueryRule("gender", "equals", "man")};
        QueryCondition<SysUser> queryCondition = new QueryCondition<>();
        queryCondition.setWhere(queryRules);
        queryCondition.setGroups(new QueryGroup[]{queryGroup});
        QueryWrapper<SysUser> wrapper = getWrapper(queryCondition);
        System.out.println(wrapper.getSqlSegment());
    }

    public static <T> QueryWrapper<T> getWrapper(QueryCondition<T> queryCondition) {
        QueryWrapper<T> wrapper = new QueryWrapper<>();
        if (ObjectUtil.isEmpty(queryCondition)){
            return wrapper;
        }
        QueryGroup[] groups = queryCondition.getGroups();
        if (groups!=null){
            for (QueryGroup group : groups) {
                wrapper.and( d -> parseWhere(d,group.getRules(),"or".equals(group.getOp())) );
            }
        }
        QueryRule[] where = queryCondition.getWhere();
        parseWhere(wrapper, where,false);
        return  wrapper;
    }

    public static <T> QueryWrapper<T> parseWhere(QueryWrapper<T> wrapper, QueryRule[] rules,boolean isGroupOr) {
        parseRules(rules, wrapper,isGroupOr);
        return wrapper;
    }


    private static <T> void parseRules(QueryRule[] rules, QueryWrapper<T> wrapper,boolean isGroupOr) {
        if (rules !=null && rules.length > 0){

            for (int i = 0; i < rules.length; i++) {
                QueryRule queryRule = rules[i];
                if ("equals".equals(queryRule.op)) {
                    wrapper.eq(StrUtil.toUnderlineCase(queryRule.getField()), queryRule.getValue());
                    if (isGroupOr && i < rules.length-1) wrapper.or();
                } else if ("like".equals(queryRule.op)) {
                    wrapper.like(StrUtil.toUnderlineCase(queryRule.getField()), queryRule.getValue());
                    if (isGroupOr && i < rules.length-1) wrapper.or();
                } else if ("between".equals(queryRule.op)) {
                    if (!queryRule.getValue().getClass().isArray() || Array.getLength(queryRule.getValue()) != 2) {
                        throw new RuntimeException("between 参数个数不等于2");
                    }
                    wrapper.between(StrUtil.toUnderlineCase(queryRule.getField()), Array.get(queryRule.getValue(), 0), Array.get(queryRule.getValue(), 1));
                    if (isGroupOr && i < rules.length-1) wrapper.or();
                } else {
                    throw new RuntimeException("sql查询条件类型未支持");
                }
            }
        }
    }



    public static class QueryRule{
        private String field;

        private String op;

        private Object value;

        public QueryRule(String field, String op, Object value) {
            this.field = field;
            this.op = op;
            this.value = value;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getOp() {
            return op;
        }

        public void setOp(String op) {
            this.op = op;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }


    public static class QueryCondition<T>{
        private QueryRule[]  where;
        private QueryGroup[] groups;
        private int pageSize = 20;
        private int pageNum = 1;

        public QueryRule[] getWhere() {
            return where;
        }

        public void setWhere(QueryRule[] where) {
            this.where = where;
        }

        public QueryGroup[] getGroups() {
            return groups;
        }

        public void setGroups(QueryGroup[] groups) {
            this.groups = groups;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public int getPageNum() {
            return pageNum;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }
    }


    public static class QueryGroup{
        public static final String OP_AND = "and";
        public static final String OP_OR = "or";
        public QueryRule[] rules;
        public String op ;

        private QueryGroup() {}

        public QueryGroup(QueryRule[] rules, String op) {
            this.rules = rules;
            this.op = op;
        }

        public QueryRule[] getRules() {
            return rules;
        }

        public void setRules(QueryRule[] rules) {
            this.rules = rules;
        }

        public String getOp() {
            return op;
        }

        public void setOp(String op) {
            this.op = op;
        }
    }
}
