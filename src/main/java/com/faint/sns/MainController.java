package com.faint.sns;

import java.security.Principal;
import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.faint.dto.CustomUserDetails;
import com.faint.service.CustomUserDetailsService;
import com.faint.service.PostService;

@Controller
public class MainController {

   @Inject
   private PostService service;
   
   @Autowired
   private CustomUserDetailsService uService;
   
   @RequestMapping(value = "/", method = RequestMethod.GET)
   public String home(Locale locale, Model model, Principal principal) {
      
      //인가받은 유저가 접속할 경우
      if(principal!=null){
         return "forward:/main";
      }
      
      //인가받지 않은 유저가 접속할 경우
      return "forward:/user/loginTest";
   }
   
   @RequestMapping(value = "/main", method = RequestMethod.GET)
   public void main(HttpServletRequest request, Model model)throws Exception{
      
      
      System.out.println("인증받고 '/'들렀다가 'main'들어옴");
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      CustomUserDetails user=(CustomUserDetails)uService.loadUserByUsername(authentication.getName());
      
      System.out.println("뭐가나오려나"+user.getVo().getEmail());
      
      //세션 생성 및 메인 피드 생성
      if(user!=null){
         
         HttpSession session=request.getSession();
         session.setAttribute("login", user.getVo());
         
         System.out.println(session.getAttribute("login").toString());
         
         model.addAttribute("list", service.mainRead(user.getVo().getId())); //세션 아이디값을 통해 현재 팔로우중인 유저들의 게시물정보 및 유저정보 등을 받아옴
         
      }else{
         System.out.println("안들어가나요?");
      }
      
   }
   
   @RequestMapping(value = "/empty", method = RequestMethod.GET)
   public void empty(Model model)throws Exception{

   }
   
/*   // 접근 제한 페이지
   @RequestMapping(value="/access-denied", method=RequestMethod.GET)
   public String accessDenied(Model model) {
      
      System.out.println("sasdsadasdsad");
      
      model.addAttribute("email", uService.getUsername());
      
      return "access-denied";
   }*/

   
   @RequestMapping(value = "/chatTest", method = RequestMethod.GET)
   public void chatTest(ModelAndView mv, Model model, HttpServletRequest request)throws Exception{
      mv.setViewName("/chatTest");
      User user=(User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
      System.out.println(user.getUsername());
      //mv.addObject("userid", user.getUsername());
      
      model.addAttribute("userid", user.getUsername());
   }

}