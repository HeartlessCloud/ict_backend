package com.laojiahuo.ictproject.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.laojiahuo.ictproject.AO.UserAO;
import com.laojiahuo.ictproject.PO.UserPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<UserPO> {

    UserPO getUserByCode(@Param("userCode") String userCode);

    void updateChatCode(@Param("chatCode") String chatCode, @Param("id") int id);

    int getTotalOfchatUserFeedbackList();

    UserPO getUserByEmail(String email);

    int userAdd(UserPO user);

    void updateUserCode(@Param("userCode") String userCode, @Param("id") Integer id);

    void chatDelete(String chatCode);


    void userInfoUpdate(UserAO request);

    void updatePassword(String password, String email);

    void userPasswordUpdate(@Param("userCode") String userCode, @Param("newPassword") String newPassword);

    int isOtherHaveEamil(@Param("userCode") String userCode, @Param("email") String email);
}
