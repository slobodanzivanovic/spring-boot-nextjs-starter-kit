package com.slobodanzivanovic.backend.util;

import java.util.List;

import org.springframework.data.domain.Page;

import lombok.Getter;

/**
 * @author Slobodan Zivanovic
 */
@Getter
public class PagedResponse<T> {

	private final int page;

	private final int size;

	private final long totalElements;

	private final int totalPages;

	private final List<T> data;

	public PagedResponse(int page, int size, long totalElements, int totalPages, List<T> data) {
		this.page = page;
		this.size = size;
		this.totalElements = totalElements;
		this.totalPages = totalPages;
		this.data = data;
	}

	public PagedResponse(Page<T> page) {
		this.page = page.getNumber();
		this.size = page.getSize();
		this.totalElements = page.getTotalElements();
		this.totalPages = page.getTotalPages();
		this.data = page.getContent();
	}

}
