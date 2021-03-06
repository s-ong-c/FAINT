package com.faint.sns;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.faint.domain.UserVO;
import com.faint.service.UserService;
import com.faint.util.Collaborative;
import com.faint.util.PageRank;

@Controller
@RequestMapping("/rankTest/*")
public class RankController {

	@Inject
	private UserService service;
	
	@RequestMapping(value = "/pagerank", method = RequestMethod.GET)
	public void pagerank(HttpServletRequest request, Model model)throws Exception{
		
		List<String> follow = service.rank();
		List<UserVO> allUser = service.listAll();
		String[][] userid=new String[allUser.size()][allUser.size()];
		PageRank p=new PageRank(allUser.size());
		
		for (int i = 0; i < allUser.size(); i++) {
			System.out.println(i+"번째 유저?"+allUser.get(i).getName());
			for (int j = 0; j < allUser.size(); j++) {
				userid[i][j]=allUser.get(i).getId()+"-"+allUser.get(j).getId();
				if(follow.contains(userid[i][j])){
					p.path[i][j]=1;
				}else if(i==j){ //동일인일 경우
					p.path[i][j]=0;
				}else{
					p.path[i][j]=0;
				}
				System.out.print(p.path[i][j]);
			}
			System.out.println();
		}
		int popular = p.calc(allUser.size());
		model.addAttribute(allUser.get(popular));
		
	}
	
	@RequestMapping(value = "/nontarget", method = RequestMethod.GET)
	public void nonTarget(HttpSession session, Model model)throws Exception{
		
		//로그인 유저
		UserVO vo=(UserVO)session.getAttribute("login");
		int loginUser = vo.getId();
		
		//전체유저
		List<String> follow = service.rank();
		List<UserVO> allUser = service.listAll();
		
		//전체유저 팔로우 관계 매핑
		HashMap<Integer, HashMap> userMap=new HashMap<Integer, HashMap>();
		
		for (int i = 0; i < allUser.size(); i++) {
			
			System.out.println(i+" 유저?"+allUser.get(i).getNickname());
			HashMap<Integer, Integer> individual=new HashMap<Integer, Integer>();
			
			for (int j = 0; j < allUser.size(); j++) {
				if(follow.contains(allUser.get(i).getId()+"-"+allUser.get(j).getId())){
					individual.put(allUser.get(j).getId(), 1);
				}else if(i==j){
					individual.put(allUser.get(j).getId(), 0);
				}else{
					individual.put(allUser.get(j).getId(), 0);
				}
			}
			
			userMap.put(allUser.get(i).getId(), individual);
			System.out.println( allUser.get(i).getId()+""+allUser.get(i).getNickname()+":  "+userMap.get(allUser.get(i).getId()) );
		}
		
		System.out.println(sim(1,3));
		
		Collaborative c = new Collaborative(loginUser);
		c.nonTargetCalc();
		
	}
	
	public double sim(double i, double j){
		return Math.sqrt(Math.pow(i, 2)+Math.pow(j, 2));
	}
	
	@RequestMapping(value = "/target", method = RequestMethod.GET)
	public void target(HttpSession session, Model model)throws Exception{
		
		//로그인 유저
		UserVO vo=(UserVO)session.getAttribute("login");
		int loginUser = vo.getId();
		
		//하드코딩 타겟유저
		int targetUser = 5;
		
		Collaborative c = new Collaborative(loginUser, targetUser);
		c.targetCalc();
		
		
	}

}
