package com.faint.service;

import java.util.List;
import java.util.Map;

import com.faint.domain.ChatroomVO;
import com.faint.domain.MessageVO;
import com.faint.dto.RelationDTO;

public interface MessageService {
	
	public int chatCreate(RelationDTO dto, MessageVO vo) throws Exception;
	
	public Map<String, Object> getMessages(int roomid, int loginid) throws Exception;
	
	public List<ChatroomVO> getChatList(int userid) throws Exception;
	
	public String registMessage(MessageVO vo) throws Exception;
}
