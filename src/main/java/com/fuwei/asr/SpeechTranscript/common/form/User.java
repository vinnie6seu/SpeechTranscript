package com.fuwei.asr.SpeechTranscript.common.form;

public class User {
    private String name;  
    private String domain;  
    private Integer age;  
    private Boolean male;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public Boolean getMale() {
		return male;
	}
	public void setMale(Boolean male) {
		this.male = male;
	}
	
	@Override
	public String toString() {
		return "User [name=" + name + ", domain=" + domain + ", age=" + age + ", male=" + male + "]";
	}  
}
