package test;

import java.io.File;

public class testDTO {
	private int num;
	private File fi1, fi2, fi3, fi4;
	public testDTO(int num, File fi1, File fi2, File fi3, File fi4) {
		super();
		this.num = num;
		this.fi1 = fi1;
		this.fi2 = fi2;
		this.fi3 = fi3;
		this.fi4 = fi4;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public File getFi1() {
		return fi1;
	}
	public void setFi1(File fi1) {
		this.fi1 = fi1;
	}
	public File getFi2() {
		return fi2;
	}
	public void setFi2(File fi2) {
		this.fi2 = fi2;
	}
	public File getFi3() {
		return fi3;
	}
	public void setFi3(File fi3) {
		this.fi3 = fi3;
	}
	public File getFi4() {
		return fi4;
	}
	public void setFi4(File fi4) {
		this.fi4 = fi4;
	}
	
	
	
}
