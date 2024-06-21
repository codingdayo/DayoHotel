package com.codingdayo.DayoHotel.service.interfac;

import com.codingdayo.DayoHotel.dto.LoginRequest;
import com.codingdayo.DayoHotel.dto.Response;
import com.codingdayo.DayoHotel.entity.User;

public interface UserService {

    Response register(User user);

    Response login(LoginRequest loginRequest);

    Response getAllUsers();

    Response getUserBookingHistory(String userId);

    Response deleteUser(String userId);

    Response getUserById(String userId);

    Response getMyInfo(String email);



}
