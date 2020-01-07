package tcc;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import tcc.flight.FlightReservationDoc;
import tcc.hotel.HotelReservationDoc;

/**
 * Simple non-transactional client. Can be used to populate the booking services
 * with some requests.
 */
public class TransactionClient {
	public static void main(String[] args) {
		try {
			Client client = ClientBuilder.newClient();
			WebTarget target = client.target(TestServer.BASE_URI);

			GregorianCalendar tomorrow = new GregorianCalendar();
			tomorrow.setTime(new Date());
			tomorrow.add(GregorianCalendar.DAY_OF_YEAR, 1);

			// flight data
			FlightReservationDoc docFlight = new FlightReservationDoc();
			docFlight.setName("Denis, Yannick, Pol");
			docFlight.setFrom("Karlsruhe");
			docFlight.setTo("Mallorca");
			docFlight.setAirline("airpalma");
			docFlight.setDate(tomorrow.getTimeInMillis());

			// hotel data
			HotelReservationDoc docHotel = new HotelReservationDoc();
			docHotel.setName("Denis, Yannick, Pol");
			docHotel.setHotel("Hilton");
			docHotel.setDate(tomorrow.getTimeInMillis());

			boolean reservationCompleted = false;
			int retryCounter = 0;

			while (!reservationCompleted || retryCounter <= 3) {
				retryCounter++;

				FlightReservationDoc responseFlight = reserveFlight(target, docFlight);
				// book hotel

				WebTarget webTargetHotel = target.path("hotel");

				Response responseHotel = webTargetHotel.request().accept(MediaType.APPLICATION_XML)
						.post(Entity.xml(docHotel));

				if (responseHotel.getStatus() != 200) {
					System.out.println("Failed : HTTP error code : " + responseHotel.getStatus());
				}

				HotelReservationDoc outputHotel = responseHotel.readEntity(HotelReservationDoc.class);
				System.out.println("Output from Server: " + outputHotel);

				if (responseFlight.getStatus() == 200 && responseHotel.getStatus() == 200) {
					// flight
					String flightId = outputFlight.getUrl().substring(outputFlight.getUrl().lastIndexOf("/") + 1);
					WebTarget flightConfirm = target.path("flight/" + flightId);
					Response flightConfirmResponse = flightConfirm.request().accept(MediaType.TEXT_PLAIN)
							.put(Entity.xml(""));

					// hotel
					String hotelId = outputHotel.getUrl().substring(outputHotel.getUrl().lastIndexOf("/") + 1);
					WebTarget hotelConfirm = target.path("hotel/" + hotelId);
					Response hotelConfirmResponse = hotelConfirm.request().accept(MediaType.TEXT_PLAIN)
							.put(Entity.xml(""));

					// either hotel or flight not confirmed
					if (flightConfirmResponse.getStatus() != 200 || hotelConfirmResponse.getStatus() != 200) {
						// rollback both transactions
						flightConfirm.request().accept(MediaType.TEXT_PLAIN).delete();
						hotelConfirm.request().accept(MediaType.TEXT_PLAIN).delete();

						System.out.println("Rollback");
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static FlightReservationDoc reserveFlight(WebTarget target, FlightReservationDoc docFlight) {
		boolean reservationCompleted = false;
		int retryCounter = 0;

		while (!reservationCompleted || retryCounter <= 3) {
			retryCounter++;

			WebTarget webTargetFlight = target.path("flight");

			Response responseFlight = webTargetFlight.request().accept(MediaType.APPLICATION_XML)
					.post(Entity.xml(docFlight));

			if (responseFlight.getStatus() == 200) {
				return responseFlight.readEntity(FlightReservationDoc.class);
			}

		}

		throw new RuntimeException();
	}
}
