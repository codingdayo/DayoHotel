package com.codingdayo.DayoHotel.service.interfac;


import com.codingdayo.DayoHotel.dto.Response;
import com.codingdayo.DayoHotel.entity.Booking;

public interface BookingService {

    Response saveBooking(Long roomId, Long userId, Booking bookingRequest);

    Response findBookingByConfirmationCode(String confirmationCode);

    Response getAllBookings();

    Response cancelBooking(Long bookingId);
}
