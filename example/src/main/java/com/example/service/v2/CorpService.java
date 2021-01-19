package com.example.service.v2;

import com.example.entity.CorpMstr;
import com.example.utils.ResultUtil;

import java.util.List;

/**
 * @author test
 */
public interface CorpService {
	ResultUtil addCorp(List<CorpMstr> list);
}
