package com.codingdayo.DayoHotel.service.interfac;

import com.codingdayo.DayoHotel.dto.Response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface RoomService {

    Response addNewRoom(String roomType,
                        BigDecimal roomPrice, String description);

    List<String> getAllRoomTypes();

    Response getAllRooms();

    Response deleteRoom(Long roomId);

    Response getRoomById(Long roomId);

    Response updateRoom(Long roomId, String roomType, BigDecimal roomPrice, String description);

    Response getAllAvailableRooms();

    Response getAvailableRoomsByDateAndType(LocalDate checkInDate,
                                            LocalDate checkOutDate,
                                            String roomType);

}
