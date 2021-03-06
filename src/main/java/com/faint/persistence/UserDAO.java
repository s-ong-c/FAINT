package com.faint.persistence;

import java.util.Date;
import java.util.List;

import com.faint.domain.SearchCriteria;
import com.faint.domain.UserVO;
import com.faint.domain.UsersException;
import com.faint.dto.RelationDTO;
import com.faint.dto.BlockedUserDTO;
import com.faint.dto.LoginDTO;

public interface UserDAO {

	public void create(UserVO vo) throws Exception;
	// ======================사용자 읽기======================
	public List<UserVO> listAll() throws Exception;
	
	public UserVO userRead(RelationDTO dto) throws Exception;

	// ======================사용자 키워드 목록(String타입의 키워드를 매개변수로 받음)======================
	public List<UserVO> listKeyword(SearchCriteria cri) throws Exception;

	//=================회원차단=================
	public void userBlock(RelationDTO dto) throws Exception;
	
	public void userUnblock(RelationDTO dto) throws Exception;
	
	public List<BlockedUserDTO> readBlockedList(int uid) throws Exception;

	// ======================팔로우====================== 
	public void flwCreate(RelationDTO dto) throws Exception;

	public void flwDelete(RelationDTO dto) throws Exception;

	public List<UserVO> flwnList(RelationDTO dto) throws Exception;

	public List<UserVO> flwdList(RelationDTO dto) throws Exception;
	
	public List<String> rank() throws Exception;

	// ======================회원가입 및 정보변경======================
	public void insertUser(UserVO vo) throws Exception;  // 회원 등
	
	public void updateUser(UserVO vo) throws Exception; // 회원수정
	
	// ======================로그인======================

	public UserVO login(LoginDTO dto) throws Exception; // 로그
	
	// 이메일로 사용자의 모든 정보 가져오기//// 추가 
	public  UserVO selectByEmail(String email) throws UsersException;
	
	
	// users_authority 테이블에 정보를 입력하기
	public void insertAuthority(UserVO users) throws UsersException;
	// 최근 등록한 사용자의 번호를 가져오기
	public Integer selectLastInsertId() throws UsersException;
	///////////////////////////////////
	
	// sessionKey & sessionLimit 업데이트
	public void keepLogin(String email, String sessionId, Date next) throws Exception; // sessinId = DB의 sessionKey

	public UserVO checkSessionKey(String value);   // 세션 확인 
	
	// ======================인증키 관련======================

	public void createAuthKey(String userEmail, String memberAuthKey) throws Exception; // 인증키 발행
	
	public UserVO chkAuth(UserVO vo) throws Exception; // 인증키 확인 

	public void userAuth(UserVO vo) throws Exception; // 인증키 로 인한 -> 유저State 변

	public UserVO authenticate(String email) throws Exception; 

	public UserVO authenticateName(String str) throws Exception;

	public void updateAuthKey(String memberEmail, String memberAuthKey) throws Exception; // 인증키 변경 및 재 발행

	public void successAuth (UserVO vo) throws Exception;  // 인증완료로 인한인증키 삭제 
	
	// ======================ip차단 관련(댓글에 한정)======================
	
	public List<String> find_ip_ban_list() throws Exception;  //	 i ip 차단 리스트 뽑
	
	public void insert_ip_ban(String ip) throws Exception;   // ip  -insert 
	
	// ======================유저정보 읽기======================

	public String getUserProfile(int id) throws Exception;  //유저 프로필 사진 

	public UserVO getUserPw(String email) throws Exception; // 유저비밀번호 등
	
	public UserVO getUserPw1(int id) throws Exception; // 유저 아이디 비번 ///
	
	// ======================회원정보 변경======================
	
	public int checkPassWord(int id, String pw) throws Exception; //비밀번호 체크
	
	public void updatePassword(UserVO vo) throws Exception; //패스워드 변경
	
	public void createTempPassword(String email, String password) throws Exception;

	public UserVO read(Integer id) throws Exception; //회원정보읽기
	
	public int chkNick(String nickname) throws Exception; //닉네임 체크

	public int update(UserVO vo) throws Exception; //유저정보 변경

	public void delete(Integer id) throws Exception; //탈퇴회원 제거
	
	public int updatePhoto(int id, String url) throws Exception; //프로필 사진 변경
	
	public UserVO naverReadUser(LoginDTO dto) throws Exception; // 네이버 유저읽기( 아직 안됨)
	
	public void naverInsertUser(LoginDTO dto) throws Exception; // 네이버 등
	
}
