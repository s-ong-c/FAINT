package com.faint.persistence;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Repository;

import com.faint.domain.PostVO;
import com.faint.domain.TagVO;
import com.faint.domain.UserVO;
import com.faint.dto.ActivityDTO;


@Repository
public class ActivityDAOImpl implements ActivityDAO {
	
	@Inject
	private SqlSession sqlSession;
	
	private static String namespace = "com.faint.mapper.ActivityMapper";
	
	
	
	//나와 친구의친구 활동표
	@Override
	public List<ActivityDTO> activityTbl(int userid) throws Exception {
		return sqlSession.selectList(namespace+".activityTbl", userid);
	}

	//추천받는 reId (List형태) 넣고 UserVo객체 받기
	@Override
	public List<UserVO> getRecomm2list(ArrayList<Integer> item) throws Exception {
				if(item.isEmpty()){   //추천할 id가 없음 (id 0 넣어서 쿼리오류 막음)
					item.add(0);
				}
		return sqlSession.selectList(namespace+".getRecomm2list", item);
	}
	
	//list타입의 uerid를 토대로 PostVO 객체 가져오기(추천계정들의 게시글)
	@Override
	public List<PostVO> getRecommPost(ArrayList<Integer> item) throws Exception {
		if(item.isEmpty()){   //추천할 id가 없음 (id 0 넣어서 쿼리오류 막음)
			item.add(0);
		}
		
		return sqlSession.selectList(namespace+".getRecommPost", item);
	}
	
	

	


}
