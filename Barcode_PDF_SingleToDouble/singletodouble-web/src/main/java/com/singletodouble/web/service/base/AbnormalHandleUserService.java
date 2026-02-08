package com.singletodouble.web.service.base;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.singletodouble.web.domain.MoAbnormalHandleUser;

public interface AbnormalHandleUserService {
	public List<MoAbnormalHandleUser> searchList(MoAbnormalHandleUser moUser);
	public MoAbnormalHandleUser searchDataById(String userQr);
    public int save(MoAbnormalHandleUser moUser);
    public int deleteById(String userQr);
    public int save(MultipartFile file, String userName);
}
