package com.cos.book.domain;

import lombok.Data;

@Data
public class CommonDto<T> {
	T data;
}
