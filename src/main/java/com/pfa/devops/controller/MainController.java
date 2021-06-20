package com.pfa.devops.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {
	Logger logger = LoggerFactory.getLogger(MainController.class);
	boolean status = true;

	@RequestMapping("/")
	public String index(){
		return "application-form.html";
	}


	@RequestMapping("/loading")
	public String loading(Model model){

	return "";
	}

/*	public void myThread(){
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				myClassList.add(new MyClass("asaad",1));
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (myClassList.size() >= 7)
					status = false;
			}
		});

		thread.start();
	}*/

}
