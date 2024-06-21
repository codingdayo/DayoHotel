package com.codingdayo.DayoHotel.service.impl;

import com.codingdayo.DayoHotel.dto.BookingDTO;
import com.codingdayo.DayoHotel.dto.Response;
import com.codingdayo.DayoHotel.entity.Booking;
import com.codingdayo.DayoHotel.entity.Room;
import com.codingdayo.DayoHotel.entity.User;
import com.codingdayo.DayoHotel.exception.OurException;
import com.codingdayo.DayoHotel.repo.BookingRepository;
import com.codingdayo.DayoHotel.repo.RoomRepository;
import com.codingdayo.DayoHotel.repo.UserRepository;
import com.codingdayo.DayoHotel.service.interfac.BookingService;
import com.codingdayo.DayoHotel.service.interfac.RoomService;
import com.codingdayo.DayoHotel.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomService roomService;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;


    @Override
    public Response saveBooking(Long roomId, Long userId, Booking bookingRequest) {
        Response response = new Response();

        try {
            if (bookingRequest.getCheckOutDate()
                    .isBefore(bookingRequest.getCheckInDate())){
                throw  new IllegalArgumentException("Check in date must come after check out date");
            }
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new OurException("Room Not Found"));
            User user = userRepository.findById(userId).orElseThrow(() -> new OurException("User Not Found"));

            List<Booking> existingBookings = room.getBookings();
            //created  roomIsAvailable and the method below
            if(!roomIsAvailable(bookingRequest, existingBookings)){
                throw new OurException("Room not Available right now");
            }

            bookingRequest.setRoom(room);
            bookingRequest.setUser(user);
            String bookingConfirmationCode = Utils.generateConfirmationCode(10);
            //missed the setting of BCcode, reason why it didn't save to the db
            bookingRequest.setBookingConfirmationCode(bookingConfirmationCode);
            bookingRepository.save(bookingRequest);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setBookingConfirmationCode(bookingConfirmationCode);

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Saving a booking: " + e.getMessage());

        }
        return response;
    }


    @Override
    public Response findBookingByConfirmationCode(String confirmationCode) {
        Response response = new Response();

        try {
            Booking booking = bookingRepository.findByBookingConfirmationCode(confirmationCode).orElseThrow(() -> new OurException("Booking not found"));
            //changed this to return roles and users; map the user with 'true'
            //BookingDTO bookingDTO = Utils.mapBookingEntityToBookingDTO(booking);
            BookingDTO bookingDTO = Utils.mapBookingEntityToBookingDTOPlusBookedRoom(booking, true);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setBooking(bookingDTO);


        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error finding a booking: " + e.getMessage());

        }
        return response;
    }

    @Override
    public Response getAllBookings() {
        Response response = new Response();

        try {
            List<Booking> booking = bookingRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
            //changed this to return roles and users
            List<BookingDTO> bookingDTOList = Utils.mapBookingListEntityToBookingListDTO(booking);
            response.setStatusCode(200);
            response.setMessage("Successful");
            response.setBookingList(bookingDTOList);


        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error getting all bookings: " + e.getMessage());

        }
        return response;
    }

    @Override
    public Response cancelBooking(Long bookingId) {
        Response response = new Response();

        try {
            bookingRepository.findById(bookingId).orElseThrow(() -> new OurException("Booking Does not Exist"));
            bookingRepository.deleteById(bookingId);
            response.setStatusCode(200);
            response.setMessage("Successful");

        } catch (OurException e) {
            response.setStatusCode(404);
            response.setMessage(e.getMessage());

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("Error Canceling a booking: " + e.getMessage());

        }
        return response;
    }




    //check this part over and over
    private boolean roomIsAvailable(Booking bookingRequest, List<Booking> existingBookings) {
        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        bookingRequest.getCheckInDate().equals(existingBooking.getCheckInDate())
                                || bookingRequest.getCheckOutDate().isBefore(existingBooking.getCheckOutDate())
                                || (bookingRequest.getCheckInDate().isAfter(existingBooking.getCheckInDate())
                                && bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckOutDate()))
                                || (bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckInDate())

                                && bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckOutDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(existingBooking.getCheckInDate()))

                                || (bookingRequest.getCheckInDate().equals(existingBooking.getCheckOutDate())
                                && bookingRequest.getCheckOutDate().equals(bookingRequest.getCheckInDate()))
                );
    }

}
