package edu.muzraev.app.model;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class User implements Serializable {
    private static final long serialVersionUID = -649487957002725321L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "user_id")
    private String userId;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "email")
    private String email;
    @Column(name = "profile_image_url")
    private String profileImageUrl;
    @Column(name = "last_login_date")
    private Date lastLoginDate;
    @Column(name = "last_login_display")
    private Date lastLoginDateDisplay;
    @Column(name = "join_date")
    private Date joinDate;
    @Column(name = "role")
    private String role;
    @Column(name = "authorities")
    private String[] authorities;
    @Column(name = "is_active")
    private boolean isActive;
//    @Column(name = "is_not_locked", columnDefinition = "true")
//    private boolean isNonLocked;

    public User() {
    }

    public User(Long id, String userId, String firstName, String lastName, String username, String password, String email, String profileImageUrl, Date lastLoginDate, Date lastLoginDateDisplay, Date joinDate, String role, String[] authorities, boolean isActive, boolean isNotLocked) {
        this.id = id;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.password = password;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.lastLoginDate = lastLoginDate;
        this.lastLoginDateDisplay = lastLoginDateDisplay;
        this.joinDate = joinDate;
        this.role = role;
        this.authorities = authorities;
        this.isActive = isActive;
//        this.isNonLocked = isNotLocked;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public Date getLastLoginDateDisplay() {
        return lastLoginDateDisplay;
    }

    public void setLastLoginDateDisplay(Date lastLoginDateDisplay) {
        this.lastLoginDateDisplay = lastLoginDateDisplay;
    }

    public Date getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(Date joinDate) {
        this.joinDate = joinDate;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String[] getAuthorities() {
        return authorities;
    }

    public void setAuthorities(String[] authorities) {
        this.authorities = authorities;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

//    public boolean isNonLocked() {
//        return isNonLocked;
//    }
//
//    public void setNonLocked(boolean nonLocked) {
//        isNonLocked = nonLocked;
//    }
}
