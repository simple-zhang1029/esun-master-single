package com.example.entity;

import java.io.Serializable;

/**
 * (UserMstr)实体类
 *
 * @author makejava
 * @since 2020-12-29 11:47:28
 */
public class UserMstr extends BaseEntity implements Serializable   {
    private static final long serialVersionUID = 482890521690347347L;


    private String userUserId;
    
    private String userCorp;
    
    private String userName;
    
    private String userLang="CH";
    
    private String userPassword;
    
    private Object userPasswdEff;
    
    private Object userLastChgDate;
    
    private Boolean userForceChange;
    
    private String userMailAddress;
    
    private String userType;
    
    private String userCountry;
    
    private Integer userFailedAttempts;
    
    private Object userLogonDate;
    
    private Integer userLogonTime;
    
    private String userLogonTimezone;
    
    private String userAccessType;
    
    private String userAccessLoc;
    
    private Boolean userActived=true;
    
    private String userActiveBy;
    
    private String userActiveReason;
    
    private Object userActiveDate;
    
    private String userRemark;
    
    private String userTzdb;
    
    private String userTimezone;
    
    private Boolean userOnlineStat;
    
    private String userSessionId;
    
    private Object userModDate;
    
    private String userModProg;
    
    private String userModUser;
    
    private String user_Chr01;
    
    private String user_Chr02;
    
    private String user_Chr03;
    
    private Integer user_Int01;
    
    private Integer user_Int02;
    
    private Integer user_Int03;
    
    private Object user_Dte01;
    
    private Object user_Dte02;
    
    private Object user_Dte03;
    
    private Object user_Dec01;
    
    private Object user_Dec02;
    
    private Object user_Dec03;
    
    private Boolean user_Log01;
    
    private Boolean user_Log02;
    
    private String userEmSystemId;
    
    private String userUser1;
    
    private String userUser2;
    
    private Boolean userRestrict;
    
    private String userSite;
    
    private String userVariantCode;
    
    private String user_Qad02;
    
    private String user_Qad01;
    
    private String user_Qadc01;
    
    private String user_Qadc02;
    
    private String user_Qadc03;
    
    private String user_Qadc04;
    
    private String user_Qadc05;
    
    private Integer user_Qadi01;
    
    private Integer user_Qadi02;
    
    private Boolean user_Qadl01;
    
    private Boolean user_Qadl02;
    
    private Boolean user_Qadl03;
    
    private Integer userModTime;
    
    private String userAddr;
    
    private String userWechat;
    
    private String userCorpcode;
    
    private String userCorpname;
    /**
    * 部门
    */
    private String userDepart;
    /**
    * 岗位
    */
    private String userPost;
    /**
    * 联系电话
    */
    private String userPhone;
    /**
    * QQ
    */
    private String userQqnum;
    /**
    * 联系人
    */
    private String userContact;
    /**
    * token盐
    */
    private String userSalt;
    /**
    * token
    */
    private String userToken;
    
    private String guid;


    public String getUserUserId() {
        return userUserId;
    }

    public void setUserUserId(String userUserId) {
        this.userUserId = userUserId;
    }

    public String getUserCorp() {
        return userCorp;
    }

    public void setUserCorp(String userCorp) {
        this.userCorp = userCorp;
    }
        
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
        
    public String getUserLang() {
        return userLang;
    }

    public void setUserLang(String userLang) {
        this.userLang = userLang;
    }
        
    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }
        
    public Object getUserPasswdEff() {
        return userPasswdEff;
    }

    public void setUserPasswdEff(Object userPasswdEff) {
        this.userPasswdEff = userPasswdEff;
    }
        
    public Object getUserLastChgDate() {
        return userLastChgDate;
    }

    public void setUserLastChgDate(Object userLastChgDate) {
        this.userLastChgDate = userLastChgDate;
    }
        
    public Boolean getUserForceChange() {
        return userForceChange;
    }

    public void setUserForceChange(Boolean userForceChange) {
        this.userForceChange = userForceChange;
    }
        
    public String getUserMailAddress() {
        return userMailAddress;
    }

    public void setUserMailAddress(String userMailAddress) {
        this.userMailAddress = userMailAddress;
    }
        
    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
        
    public String getUserCountry() {
        return userCountry;
    }

    public void setUserCountry(String userCountry) {
        this.userCountry = userCountry;
    }
        
    public Integer getUserFailedAttempts() {
        return userFailedAttempts;
    }

    public void setUserFailedAttempts(Integer userFailedAttempts) {
        this.userFailedAttempts = userFailedAttempts;
    }
        
    public Object getUserLogonDate() {
        return userLogonDate;
    }

    public void setUserLogonDate(Object userLogonDate) {
        this.userLogonDate = userLogonDate;
    }
        
    public Integer getUserLogonTime() {
        return userLogonTime;
    }

    public void setUserLogonTime(Integer userLogonTime) {
        this.userLogonTime = userLogonTime;
    }
        
    public String getUserLogonTimezone() {
        return userLogonTimezone;
    }

    public void setUserLogonTimezone(String userLogonTimezone) {
        this.userLogonTimezone = userLogonTimezone;
    }
        
    public String getUserAccessType() {
        return userAccessType;
    }

    public void setUserAccessType(String userAccessType) {
        this.userAccessType = userAccessType;
    }
        
    public String getUserAccessLoc() {
        return userAccessLoc;
    }

    public void setUserAccessLoc(String userAccessLoc) {
        this.userAccessLoc = userAccessLoc;
    }
        
    public Boolean getUserActived() {
        return userActived;
    }

    public void setUserActived(Boolean userActived) {
        this.userActived = userActived;
    }
        
    public String getUserActiveBy() {
        return userActiveBy;
    }

    public void setUserActiveBy(String userActiveBy) {
        this.userActiveBy = userActiveBy;
    }
        
    public String getUserActiveReason() {
        return userActiveReason;
    }

    public void setUserActiveReason(String userActiveReason) {
        this.userActiveReason = userActiveReason;
    }
        
    public Object getUserActiveDate() {
        return userActiveDate;
    }

    public void setUserActiveDate(Object userActiveDate) {
        this.userActiveDate = userActiveDate;
    }
        
    public String getUserRemark() {
        return userRemark;
    }

    public void setUserRemark(String userRemark) {
        this.userRemark = userRemark;
    }
        
    public String getUserTzdb() {
        return userTzdb;
    }

    public void setUserTzdb(String userTzdb) {
        this.userTzdb = userTzdb;
    }
        
    public String getUserTimezone() {
        return userTimezone;
    }

    public void setUserTimezone(String userTimezone) {
        this.userTimezone = userTimezone;
    }
        
    public Boolean getUserOnlineStat() {
        return userOnlineStat;
    }

    public void setUserOnlineStat(Boolean userOnlineStat) {
        this.userOnlineStat = userOnlineStat;
    }
        
    public String getUserSessionId() {
        return userSessionId;
    }

    public void setUserSessionId(String userSessionId) {
        this.userSessionId = userSessionId;
    }
        
    public Object getUserModDate() {
        return userModDate;
    }

    public void setUserModDate(Object userModDate) {
        this.userModDate = userModDate;
    }
        
    public String getUserModProg() {
        return userModProg;
    }

    public void setUserModProg(String userModProg) {
        this.userModProg = userModProg;
    }
        
    public String getUserModUser() {
        return userModUser;
    }

    public void setUserModUser(String userModUser) {
        this.userModUser = userModUser;
    }
        
    public String getUser_Chr01() {
        return user_Chr01;
    }

    public void setUser_Chr01(String user_Chr01) {
        this.user_Chr01 = user_Chr01;
    }
        
    public String getUser_Chr02() {
        return user_Chr02;
    }

    public void setUser_Chr02(String user_Chr02) {
        this.user_Chr02 = user_Chr02;
    }
        
    public String getUser_Chr03() {
        return user_Chr03;
    }

    public void setUser_Chr03(String user_Chr03) {
        this.user_Chr03 = user_Chr03;
    }
        
    public Integer getUser_Int01() {
        return user_Int01;
    }

    public void setUser_Int01(Integer user_Int01) {
        this.user_Int01 = user_Int01;
    }
        
    public Integer getUser_Int02() {
        return user_Int02;
    }

    public void setUser_Int02(Integer user_Int02) {
        this.user_Int02 = user_Int02;
    }
        
    public Integer getUser_Int03() {
        return user_Int03;
    }

    public void setUser_Int03(Integer user_Int03) {
        this.user_Int03 = user_Int03;
    }
        
    public Object getUser_Dte01() {
        return user_Dte01;
    }

    public void setUser_Dte01(Object user_Dte01) {
        this.user_Dte01 = user_Dte01;
    }
        
    public Object getUser_Dte02() {
        return user_Dte02;
    }

    public void setUser_Dte02(Object user_Dte02) {
        this.user_Dte02 = user_Dte02;
    }
        
    public Object getUser_Dte03() {
        return user_Dte03;
    }

    public void setUser_Dte03(Object user_Dte03) {
        this.user_Dte03 = user_Dte03;
    }
        
    public Object getUser_Dec01() {
        return user_Dec01;
    }

    public void setUser_Dec01(Object user_Dec01) {
        this.user_Dec01 = user_Dec01;
    }
        
    public Object getUser_Dec02() {
        return user_Dec02;
    }

    public void setUser_Dec02(Object user_Dec02) {
        this.user_Dec02 = user_Dec02;
    }
        
    public Object getUser_Dec03() {
        return user_Dec03;
    }

    public void setUser_Dec03(Object user_Dec03) {
        this.user_Dec03 = user_Dec03;
    }
        
    public Boolean getUser_Log01() {
        return user_Log01;
    }

    public void setUser_Log01(Boolean user_Log01) {
        this.user_Log01 = user_Log01;
    }
        
    public Boolean getUser_Log02() {
        return user_Log02;
    }

    public void setUser_Log02(Boolean user_Log02) {
        this.user_Log02 = user_Log02;
    }
        
    public String getUserEmSystemId() {
        return userEmSystemId;
    }

    public void setUserEmSystemId(String userEmSystemId) {
        this.userEmSystemId = userEmSystemId;
    }
        
    public String getUserUser1() {
        return userUser1;
    }

    public void setUserUser1(String userUser1) {
        this.userUser1 = userUser1;
    }
        
    public String getUserUser2() {
        return userUser2;
    }

    public void setUserUser2(String userUser2) {
        this.userUser2 = userUser2;
    }
        
    public Boolean getUserRestrict() {
        return userRestrict;
    }

    public void setUserRestrict(Boolean userRestrict) {
        this.userRestrict = userRestrict;
    }
        
    public String getUserSite() {
        return userSite;
    }

    public void setUserSite(String userSite) {
        this.userSite = userSite;
    }
        
    public String getUserVariantCode() {
        return userVariantCode;
    }

    public void setUserVariantCode(String userVariantCode) {
        this.userVariantCode = userVariantCode;
    }
        
    public String getUser_Qad02() {
        return user_Qad02;
    }

    public void setUser_Qad02(String user_Qad02) {
        this.user_Qad02 = user_Qad02;
    }
        
    public String getUser_Qad01() {
        return user_Qad01;
    }

    public void setUser_Qad01(String user_Qad01) {
        this.user_Qad01 = user_Qad01;
    }
        
    public String getUser_Qadc01() {
        return user_Qadc01;
    }

    public void setUser_Qadc01(String user_Qadc01) {
        this.user_Qadc01 = user_Qadc01;
    }
        
    public String getUser_Qadc02() {
        return user_Qadc02;
    }

    public void setUser_Qadc02(String user_Qadc02) {
        this.user_Qadc02 = user_Qadc02;
    }
        
    public String getUser_Qadc03() {
        return user_Qadc03;
    }

    public void setUser_Qadc03(String user_Qadc03) {
        this.user_Qadc03 = user_Qadc03;
    }
        
    public String getUser_Qadc04() {
        return user_Qadc04;
    }

    public void setUser_Qadc04(String user_Qadc04) {
        this.user_Qadc04 = user_Qadc04;
    }
        
    public String getUser_Qadc05() {
        return user_Qadc05;
    }

    public void setUser_Qadc05(String user_Qadc05) {
        this.user_Qadc05 = user_Qadc05;
    }
        
    public Integer getUser_Qadi01() {
        return user_Qadi01;
    }

    public void setUser_Qadi01(Integer user_Qadi01) {
        this.user_Qadi01 = user_Qadi01;
    }
        
    public Integer getUser_Qadi02() {
        return user_Qadi02;
    }

    public void setUser_Qadi02(Integer user_Qadi02) {
        this.user_Qadi02 = user_Qadi02;
    }
        
    public Boolean getUser_Qadl01() {
        return user_Qadl01;
    }

    public void setUser_Qadl01(Boolean user_Qadl01) {
        this.user_Qadl01 = user_Qadl01;
    }
        
    public Boolean getUser_Qadl02() {
        return user_Qadl02;
    }

    public void setUser_Qadl02(Boolean user_Qadl02) {
        this.user_Qadl02 = user_Qadl02;
    }
        
    public Boolean getUser_Qadl03() {
        return user_Qadl03;
    }

    public void setUser_Qadl03(Boolean user_Qadl03) {
        this.user_Qadl03 = user_Qadl03;
    }
        
    public Integer getUserModTime() {
        return userModTime;
    }

    public void setUserModTime(Integer userModTime) {
        this.userModTime = userModTime;
    }
        
    public String getUserAddr() {
        return userAddr;
    }

    public void setUserAddr(String userAddr) {
        this.userAddr = userAddr;
    }
        
    public String getUserWechat() {
        return userWechat;
    }

    public void setUserWechat(String userWechat) {
        this.userWechat = userWechat;
    }
        
    public String getUserCorpcode() {
        return userCorpcode;
    }

    public void setUserCorpcode(String userCorpcode) {
        this.userCorpcode = userCorpcode;
    }
        
    public String getUserCorpname() {
        return userCorpname;
    }

    public void setUserCorpname(String userCorpname) {
        this.userCorpname = userCorpname;
    }
        
    public String getUserDepart() {
        return userDepart;
    }

    public void setUserDepart(String userDepart) {
        this.userDepart = userDepart;
    }
        
    public String getUserPost() {
        return userPost;
    }

    public void setUserPost(String userPost) {
        this.userPost = userPost;
    }
        
    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }
        
    public String getUserQqnum() {
        return userQqnum;
    }

    public void setUserQqnum(String userQqnum) {
        this.userQqnum = userQqnum;
    }
        
    public String getUserContact() {
        return userContact;
    }

    public void setUserContact(String userContact) {
        this.userContact = userContact;
    }
        
    public String getUserSalt() {
        return userSalt;
    }

    public void setUserSalt(String userSalt) {
        this.userSalt = userSalt;
    }
        
    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }
        
    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

}