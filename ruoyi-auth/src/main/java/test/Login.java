package test;


import com.ruoyi.auth.RuoYiAuthApplication;
import com.ruoyi.auth.controller.TokenController;
import com.ruoyi.auth.form.LoginBody;
import com.ruoyi.common.core.domain.R;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = {RuoYiAuthApplication.class})
@RunWith(SpringRunner.class)
public class Login {

    @Autowired
    private TokenController tokenController;

    @Test
    public void getLogin(){
        R<?> login = tokenController.login(new LoginBody("admin", "admin123"));
        System.out.println(login.getData());
    }
}
