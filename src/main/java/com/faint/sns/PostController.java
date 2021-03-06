package com.faint.sns;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.faint.domain.PostVO;
import com.faint.domain.UserVO;
import com.faint.dto.FollowinPostDTO;
import com.faint.dto.RelationDTO;
import com.faint.service.PostService;

@Controller
@RequestMapping("/post/*")
public class PostController {

	@Inject
	private PostService service;
	
	private static final Logger logger = LoggerFactory.getLogger(PostController.class);
	/*게시물 등록창 읽기*/
	@RequestMapping(value = "/register", method = RequestMethod.GET)
	public String registerGET(Model model, HttpServletRequest request) throws Exception {
		logger.info("register get..............");
		
		HttpSession session = request.getSession();
		if(session !=null){
			UserVO user = (UserVO)session.getAttribute("login");
			logger.info(user.toString());
			model.addAttribute("userVO", user);
		}
		
		return "forward:/post/uploader";
	}

	/*게시물 등록 - model방식*/
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public void registerPOST(Model model, HttpServletRequest request) throws Exception {
		logger.info("register post..............");
		
		HttpSession session = request.getSession();
		if(session !=null){
			UserVO user = (UserVO)session.getAttribute("login");
			logger.info(user.toString());
			model.addAttribute("userVO", user);
		}
		
		String[] files = request.getParameterValues("files");

		if(files != null){
			JSONArray jArray = new JSONArray();

			for(int i=0; i<files.length;i++){
				JSONParser jsonParser = new JSONParser();
				JSONObject jsonObject = (JSONObject) jsonParser.parse(files[i]);
				jArray.add(jsonObject);
			}
			logger.info(jArray.toJSONString());
			model.addAttribute("files", jArray);
		}
		logger.info(Arrays.toString(files));
	}
	
	// 게시물 수정 get-1
	@RequestMapping(value = "/{postid}/postEditor", method = RequestMethod.GET)
	public String modifyGET(@PathVariable("postid") int postid, Model model, HttpServletRequest request) throws Exception {
		
		UserVO vo=(UserVO)request.getSession().getAttribute("login");
		RelationDTO dto=new RelationDTO();
		dto.setLoginid(vo.getId());
		dto.setPostid(postid);
		
		//이미지 확장자 리스트
		List<String> imageType = Arrays.asList("jpg", "bmp", "gif", "png", "jpeg");
		//비디오 확장자 리스트
		List<String> videoType = Arrays.asList("avi", "mp4", "mpg", "mpeg", "mpe", "wmv", "asf", "flv", "ogg");
		
		FollowinPostDTO post=(FollowinPostDTO)service.detailRead(dto);
		if(vo.getId()!=post.getUserid()){
			return "forward:/empty";
		}else{
			String[] url = post.getUrl().split("\\|");
			String[] filter = post.getFilter().split("\\|");
			JSONArray jArray = new JSONArray();
			for(int i =0; i<url.length;i++){
				String tmp = url[i].substring(url[i].lastIndexOf('.')+1);
				
				String type ="";
				//파일 타입체크
				if(imageType.contains(tmp.toLowerCase())){
					type = "image";
				}else if (videoType.contains(tmp.toLowerCase())){
					type = "video";
				}
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("fileUrl", url[i]);
				jsonObj.put("fileType", type);
				jsonObj.put("filter", filter[i]);
				jArray.add(jsonObj);
			}
			
			//캡션 <br> \n 으로 치환
			post.setCaption(post.getCaption().replaceAll(" <br> ", "\n"));
			logger.info(jArray.toJSONString());
			model.addAttribute("files", jArray);
			model.addAttribute("postVO", post);
			System.out.println(post.toString());
			return "forward:/post/modify";
		}

	}
	
	// 게시물 수정 get-2
	@RequestMapping(value="/modify", method=RequestMethod.GET)
	public void profile(Model model, HttpServletRequest request) throws Exception{
		
	}
	
	// 게시물 수정 post
	@RequestMapping(value = "/modify", method = RequestMethod.POST)
	public String modifyPOST(PostVO post, RedirectAttributes rttr, HttpServletRequest request) throws Exception {
		System.out.println("===================modify post.........=========");
		HttpSession session = request.getSession();
		UserVO user = (UserVO)session.getAttribute("login");
		//게시글 유저정보랑 로그인 유저랑 다를경우 리턴
		//같을떄만 수정 가능
		if(post.getUserid() != user.getId() ){
			
			rttr.addFlashAttribute("msg", "fail");
		}else{
			
			System.out.println(post.toString());
			service.modify(post);
			rttr.addFlashAttribute("msg", "SUCCESS");
		}

		return "redirect:/member/"+user.getNickname();
	}
	
	//게시물 삭제
	@RequestMapping(value="/{postid}/delete", method=RequestMethod.DELETE)
	public ResponseEntity<String> deletePost(@PathVariable("postid") int postid, HttpServletRequest request) throws Exception {
		ResponseEntity<String> entity=null;
		
		UserVO vo=(UserVO)request.getSession().getAttribute("login");
		
		try{
			String delete=service.deleteOne(postid, vo.getId());
			System.out.println(delete);
			if(delete=="SUCCESS"){
				entity=new ResponseEntity<String>("SUCCESS", HttpStatus.OK);	
			}else{
				entity=new ResponseEntity<String>("FAIL", HttpStatus.BAD_REQUEST);
			}
		}catch(Exception e){
			e.printStackTrace();
			entity=new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
	
	/*파일 첨부*/
	@RequestMapping(value = "/uploader", method = RequestMethod.GET)
	public void uploader() throws Exception {
		logger.info("uploader get..............");
		
	}
	
	/*게시물 등록 - model방식*/
	@RequestMapping(value = "/register/submit", method = RequestMethod.POST)
	public String registerSubmit(PostVO post, RedirectAttributes rttr, HttpServletRequest request) throws Exception {
		logger.info("regist submit POST..............");
		logger.info(post.toString());
		
		HttpSession session = request.getSession();
		System.out.println(Arrays.toString(post.getFilters()));
		UserVO user = (UserVO)session.getAttribute("login");
		
		post.setUserid(user.getId());
		
		service.regist(post);

		rttr.addFlashAttribute("msg", "SUCCESS");

		return "redirect:/main";
	}
	
	
	/*게시물 URI*/
	@ResponseBody
	@RequestMapping(value="/{userid}", method=RequestMethod.GET)
	public ResponseEntity<List<PostVO>> list(@PathVariable("userid") Integer userid){
		ResponseEntity<List<PostVO>> entity=null;
		try{
			entity=new ResponseEntity<>(service.read(userid), HttpStatus.OK);
		}catch(Exception e){
			e.printStackTrace();
			entity=new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
	
	@ResponseBody
	@RequestMapping(value="/detail", method=RequestMethod.POST)
	public ResponseEntity<FollowinPostDTO> detailRead(@RequestParam("postid") Integer postid, HttpServletRequest request)throws Exception{
		
		HttpSession session=request.getSession();
		UserVO userVO=(UserVO)session.getAttribute("login");
		
		RelationDTO dto = new RelationDTO();
		dto.setPostid(postid);
		dto.setLoginid(userVO.getId());
		
		ResponseEntity<FollowinPostDTO> entity=null;
		try{
		
			entity=new ResponseEntity<>(service.detailRead(dto), HttpStatus.OK);
		}catch(Exception e){
			e.printStackTrace();
			entity=new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
	
	//게시물 저장 - rest방식
	@RequestMapping(value="/{postid}/store", method=RequestMethod.POST)
	public ResponseEntity<String> postStore(@PathVariable("postid") Integer postid, HttpServletRequest request)throws Exception{
		ResponseEntity<String> entity=null;
		
		HttpSession session=request.getSession();
		UserVO userVO=(UserVO)session.getAttribute("login");
		RelationDTO dto = new RelationDTO();
		dto.setPostid(postid);
		dto.setLoginid(userVO.getId());
				
		try{
			service.postStore(dto);
			entity=new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
		}catch(Exception e){
			e.printStackTrace();
			entity=new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return entity;
	}

	//게시물 저장 취소 - rest방식
	@RequestMapping(value="/{postid}/takeaway", method=RequestMethod.DELETE)
	public ResponseEntity<String> postTakeaway(@PathVariable("postid") Integer postid, HttpServletRequest request)throws Exception{
		ResponseEntity<String> entity=null;
		
		HttpSession session=request.getSession();
		UserVO userVO=(UserVO)session.getAttribute("login");
		RelationDTO dto = new RelationDTO();
		dto.setPostid(postid);
		dto.setLoginid(userVO.getId());
		
		try{
			service.postTakeaway(dto);
			entity=new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
		}catch(Exception e){
			e.printStackTrace();
			entity=new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
	
	//like 등록 - rest방식
	@RequestMapping(value="/{postid}/like", method=RequestMethod.POST)
	public ResponseEntity<String> postLike(@PathVariable("postid") Integer postid, HttpServletRequest request)throws Exception{
		ResponseEntity<String> entity=null;
		
		HttpSession session=request.getSession();
		UserVO userVO=(UserVO)session.getAttribute("login");
		RelationDTO dto = new RelationDTO();
		dto.setPostid(postid);
		dto.setLoginid(userVO.getId());
		
		try{
			service.postLike(dto);
			entity=new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
		}catch(Exception e){
			e.printStackTrace();
			entity=new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return entity;
	}

	//like 삭제 - rest방식
	@RequestMapping(value="/{postid}/unlike", method=RequestMethod.DELETE)
	public ResponseEntity<String> postUnlike(@PathVariable("postid") Integer postid, HttpServletRequest request)throws Exception{
		ResponseEntity<String> entity=null;
		
		HttpSession session=request.getSession();
		UserVO userVO=(UserVO)session.getAttribute("login");
		RelationDTO dto = new RelationDTO();
		dto.setPostid(postid);
		dto.setLoginid(userVO.getId());
		
		try{
			service.postUnlike(dto);
			entity=new ResponseEntity<String>("SUCCESS", HttpStatus.OK);
		}catch(Exception e){
			e.printStackTrace();
			entity=new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
	
	//게시물에 대한 좋아요 유저반환(PK,nickname만) - JSON객체(LIST배열에 담아서 던져줌)
	@ResponseBody
	@RequestMapping(value="/{postid}/likerlist", method=RequestMethod.GET)
	public ResponseEntity<List<UserVO>> likerList(@PathVariable("postid") Integer postid, HttpServletRequest request)throws Exception{
		ResponseEntity<List<UserVO>> entity=null;
		
		HttpSession session=request.getSession();
		UserVO userVO=(UserVO)session.getAttribute("login");
		RelationDTO dto = new RelationDTO();
		dto.setPostid(postid);
		dto.setLoginid(userVO.getId());
		
		try{
			entity=new ResponseEntity<List<UserVO>>(service.postLiker(dto), HttpStatus.OK);
		}catch(Exception e){
			e.printStackTrace();
			entity=new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		return entity;
	}
	
}
