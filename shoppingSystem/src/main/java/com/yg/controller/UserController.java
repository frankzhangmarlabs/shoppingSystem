package com.yg.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yg.model.User;
import com.yg.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@ResponseBody
	@RequestMapping("/findAll")
	public List<User> findAll(@RequestParam(value = "type") String type) {
		logger.info("根据用户类型查询用户列表");
		if ("0".equals(type)) {
			return userService.findAll();
		} else if ("1".equals(type)) {
			String role = "manager";
			return userService.findUserByRole(role);
		} else {
			String role = "typical-user";
			return userService.findUserByRole(role);
		}
	}

	@ResponseBody
	@RequestMapping("/findUserByRole")
	public List<User> findUserByRole() {
		String role = "manager";
		return userService.findUserByRole(role);
	}

	@ResponseBody
	@RequestMapping("/findUserById")
	public User findUserById(@RequestParam(value = "id") String id) {
		Integer ID = Integer.parseInt(id);
		return userService.findUserById(ID);

	}

	@ResponseBody
	@RequestMapping("/updateUser")
	public boolean updateUser(@RequestParam(value = "id") String id, @RequestParam(value = "username") String username,
			@RequestParam(value = "password") String password, @RequestParam(value = "mobile") String mobile,
			@RequestParam(value = "mail") String mail, @RequestParam(value = "birthday") String birthday,
			@RequestParam(value = "sex") String sex, @RequestParam(value = "role") String role) throws ParseException {
		Integer ID = Integer.parseInt(id);
		User user = userService.findUserById(ID);
		//if(birthday != ""){
		if(!"".equals(birthday)){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			Date date = sdf.parse(birthday);
			user.setBirthday(date);
		}else{
			user.setBirthday(null);
		}
		user.setId(Integer.parseInt(id));
		user.setUsername(username);
		user.setPassword(password);
		user.setMobile(mobile);
		user.setMail(mail);
		user.setSex(sex);
		user.setRole(role);
		//System.out.println(user);
		return userService.updateUser(user) > 0;
	}

	@ResponseBody
	@RequestMapping("/insertUser")
	public boolean insertUser(@RequestParam(value = "username") String username,
			@RequestParam(value = "password") String password, @RequestParam(value = "mobile") String mobile,
			@RequestParam(value = "mail") String mail, @RequestParam(value = "birthday") String birthday,
			@RequestParam(value = "sex") String sex, @RequestParam(value = "role") String role) throws ParseException {
		User user = new User();
		if(birthday != null){
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
			Date date = sdf.parse(birthday);
			user.setBirthday(date);
		}
		user.setUsername(username);
		user.setPassword(password);
		user.setMobile(mobile);
		user.setMail(mail);
		user.setSex(sex);
		user.setRole(role);
		//System.out.println(user);
		return userService.insertUser(user) > 0;
	}

	@ResponseBody
	@RequestMapping("/deleteUser")
	public boolean deleteUser(String delId) {
		Integer id = Integer.parseInt(delId);
		return userService.deleteUser(id) > 0;
	}
	
	@ResponseBody
	@RequestMapping("/findUserByUsername")
	public User findUserByUsername(@RequestParam(value = "username") String username) {
			logger.info("#######查找当前用户");
			return userService.findUserByUsername(username);
	}
	
	@ResponseBody
	@RequestMapping("/registerUser")
	public boolean registerUser(@RequestParam(value = "username") String username,
			@RequestParam(value = "password") String password){
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		user.setRole("typical-user");
		return userService.registerUser(user) > 0;	
	}
	
	@ResponseBody
	@RequestMapping("/modifyPassword")
	public boolean modifyPassword(@RequestParam(value = "username") String username,
			@RequestParam(value = "password2") String password2){
		User user = userService.findUserByUsername(username);
		user.setPassword(password2);
		return userService.updateUser(user) > 0;
	}
	
	@ResponseBody
	@RequestMapping("/edGridSearch")
	public List<User> edGridSearch(){
		logger.info("#######angularJS自带的表格查询");
		return userService.findAll();
	}
	
	public User loginUser(@RequestParam(value = "username") String username,
			@RequestParam(value = "password") String password, HttpServletRequest request ){
		logger.info("#######登陆验证");
		User loginUser = userService.loginUser(username, password);
		if(loginUser != null){
			HttpSession session = request.getSession();
			session.setAttribute("userInfo", loginUser);//将登陆信息存入session
			User user = new User();
			user.setId(loginUser.getId());
			user.setUsername(loginUser.getUsername());
			user.setRole(loginUser.getRole());
			return user;
		}else{
			return null;
		}	
	}
}
