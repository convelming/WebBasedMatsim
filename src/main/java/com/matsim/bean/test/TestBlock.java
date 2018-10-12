package com.matsim.bean.test;

public class TestBlock {

    private String type;
    private int id;
    private int status;
    
    private int svgId;
    private int svgShapeId;
    
    private TestPosition position;
    private TestConnect[] connects;
    
    
    



    
	public TestPosition getPosition() {
		return position;
	}
	public void setPosition(TestPosition position) {
		this.position = position;
	}
	public int getSvgId() {
		return svgId;
	}
	public void setSvgId(int svgId) {
		this.svgId = svgId;
	}
	public int getSvgShapeId() {
		return svgShapeId;
	}
	public void setSvgShapeId(int svgShapeId) {
		this.svgShapeId = svgShapeId;
	}
	public TestConnect[] getConnects() {
		return connects;
	}
	public void setConnects(TestConnect[] connects) {
		this.connects = connects;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}


	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}

	
}
