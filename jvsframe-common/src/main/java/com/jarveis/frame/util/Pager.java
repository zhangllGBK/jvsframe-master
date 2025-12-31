package com.jarveis.frame.util;

import java.util.List;

/**
 * 分页类
 * 
 * @author liuguojun
 */
public class Pager {

	private int pagesize = 10; // 页面数据大小
	private int curpage = 1; // 当前页
	private int sum; // 总记录
	private int pages; // 总页数
	private int start; // 获取数据的开始索引
	private int end; // 获取数据的结束索引
	private List<Object> data; // 获取的数据
	private String sort; // 排序类型
	private String order = "asc"; // 排序字段

	public int getCurpage() {
		return curpage;
	}

	public void setCurpage(int curpage) {
		if (curpage < 1) {
			curpage = 1;
		}
		this.curpage = curpage;
	}

	public int getEnd() {
		end = this.getStart() + pagesize;
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

	public int getPages() {
		if (sum == 0) {
			return 1;
		}
		if (sum % pagesize == 0) {
			pages = sum / pagesize;
		} else {
			pages = sum / pagesize + 1;
		}
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}

	public int getPagesize() {
		return pagesize;
	}

	public void setPagesize(int pagesize) {
		this.pagesize = pagesize;
	}

	public int getStart() {
		start = (this.getCurpage() - 1) * pagesize;
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getSum() {
		return sum;
	}

	public void setSum(int sum) {
		this.sum = sum;
	}

	public List<Object> getData() {
		return data;
	}

	public void setData(List<Object> data) {
		this.data = data;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}
}
