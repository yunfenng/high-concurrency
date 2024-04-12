package cn.jaa.im.common.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author: Jaa
 * @Description:
 * @Date 2024/4/12
 */
@Data
public class User implements Serializable {
    private String uid;         // 用户id
    private String nickName;    // 昵称

    public User(String uid, String nickName) {
        this.uid = uid;
        this.nickName = nickName;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + getUid() + '\'' +
                ", nickName='" + getNickName() + '\'' +
                '}';
    }
}
