package com.artlongs.sys.controller;

import act.controller.annotation.TemplateContext;
import act.controller.annotation.UrlContext;
import act.view.RenderAny;
import com.artlongs.framework.controller.BaseController;
import com.artlongs.framework.vo.R;
import com.artlongs.sys.model.SysUser;
import com.artlongs.sys.service.SysMenuService;
import com.artlongs.sys.service.SysUserService;
import org.osgl.http.H;
import org.osgl.mvc.annotation.GetAction;
import org.osgl.mvc.annotation.PostAction;
import org.osgl.mvc.result.RenderJSON;

import javax.inject.Inject;

/**
 * Function: 后台--登录
 *
 * @Autor: leeton
 * @Date : 11/20/17
 */
@UrlContext("/sys/login")
@TemplateContext("/sys/login")
public class SysLoginController extends BaseController {

    @Inject
    private SysUserService sysUserService;
    @Inject
    private SysMenuService sysMenuService;

    @GetAction("")
    public RenderAny login() {
        ctx.renderArg("loginMsg", "");
        return render("login.html");
    }

    @PostAction("post.json")
    public RenderJSON loginPostJson(String userName ,String pwd,H.Session session,H.Response response) {
        return json(loginCheck(userName ,pwd,session,response));
    }

    @PostAction("post")
    public RenderAny loginPost(String userName ,String pwd,H.Session session,H.Response response) {
        R vo = loginCheck(userName ,pwd,session,response);
        if(vo.isSucc()){
            to("/sys/home");
        }
        ctx.renderArg("loginMsg", vo.getMsg());
       return render("login.html");
    }

    @GetAction("out")
    public void out() {
        ctx.logout();
        sysMenuService.clear();
        to("/sys/login");
    }


    private R loginCheck(String userName , String pwd, H.Session session, H.Response response){
        R<SysUser> r = sysUserService.checkLogin(userName, pwd);
        if (r.isSucc()) { //用户登录检查成功,保存到session
            SysUser sysUser = r.getItem();
            sysUser.saveToSession(session);
            //写cookie
            response.addCookie(sysUser.buildMyCookie(sysUser.getId().toString()));
            r.getItem().setPwd("");
        }
        return r;
    }



}
