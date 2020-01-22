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
	public static int MAX_RETRIES = 3;

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

			// reserving flight & hotel
			try {
				// reserve flight
				FlightReservationDoc responseFlight = reserveFlight(target, docFlight);

				// reserve hotel
				HotelReservationDoc responseHotel = reserveHotel(target, docHotel);

				try {
					// confirming flight
					confirmFlight(target, responseFlight.getUrl());

					try {
						// confirming hotel
						confirmHotel(target, responseHotel.getUrl());
					} catch (Exception e3) {
						System.out.println("Send Email");
					}
				} catch (Exception e) {
					System.out.println(e);

					try {
						// something went wrong, rollback transactions
						rollbackFlightConfirmation(target, responseFlight.getUrl());
						rollbackHotelConfirmation(target, responseHotel.getUrl());
					} catch (Exception e2) {
						System.out.println(e2);
					}

				}
			} catch (Exception e) {
				System.out.println(e);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static FlightReservationDoc reserveFlight(WebTarget target, FlightReservationDoc docFlight)
			throws Exception {
		int retryCounter = 0;

		while (retryCounter <= MAX_RETRIES) {
			retryCounter++;

			WebTarget webTargetFlight = target.path("flight");

			Response responseFlight = webTargetFlight.request().accept(MediaType.APPLICATION_XML)
					.post(Entity.xml(docFlight));

			if (responseFlight.getStatus() == 200) {
				return responseFlight.readEntity(FlightReservationDoc.class);
			}
		}

		throw new Exception("Reserving flight not possible!");
	}

	private static HotelReservationDoc reserveHotel(WebTarget target, HotelReservationDoc docHotel) throws Exception {
		int retryCounter = 0;

		while (retryCounter <= MAX_RETRIES) {
			retryCounter++;

			WebTarget webTargetHotel = target.path("hotel");

			Response responseHotel = webTargetHotel.request().accept(MediaType.APPLICATION_XML)
					.post(Entity.xml(docHotel));

			if (responseHotel.getStatus() == 200) {
				return responseHotel.readEntity(HotelReservationDoc.class);
			}
		}

		throw new Exception("Reserving hotel not possible!");
	}

	private static boolean confirmFlight(WebTarget target, String flightConfirmationUrl) throws Exception {
		int retryCounter = 0;

		while (retryCounter <= MAX_RETRIES) {
			retryCounter++;

			String flightConfirmationId = flightConfirmationUrl.substring(flightConfirmationUrl.lastIndexOf("/") + 1);
			WebTarget flightConfirmationTarget = target.path("flight/" + flightConfirmationId);
			Response flightConfirmationResponse = flightConfirmationTarget.request().accept(MediaType.TEXT_PLAIN)
					.put(Entity.xml(""));

			if (flightConfirmationResponse.getStatus() == 200) {
				return true;
			}
		}

		throw new Exception("Confirming flight not possible!");
	}

	private static boolean confirmHotel(WebTarget target, String hotelConfirmationUrl) throws Exception {
		int retryCounter = 0;

		while (retryCounter <= 10) {
			retryCounter++;

			String hotelConfirmationId = hotelConfirmationUrl.substring(hotelConfirmationUrl.lastIndexOf("/") + 1);
			WebTarget hotelConfirmationTarget = target.path("hotel/" + hotelConfirmationId);
			Response hotelConfirmationResponse = hotelConfirmationTarget.request().accept(MediaType.TEXT_PLAIN)
					.put(Entity.xml(""));

			if (hotelConfirmationResponse.getStatus() == 200) {
				return true;
			}
		}

		throw new Exception("Confirming hotel not possible!");
	}

	private static boolean rollbackFlightConfirmation(WebTarget target, String flightConfirmationUrl) throws Exception {
		int retryCounter = 0;

		while (retryCounter <= MAX_RETRIES) {
			retryCounter++;

			String flightConfirmationId = flightConfirmationUrl.substring(flightConfirmationUrl.lastIndexOf("/") + 1);
			WebTarget flightConfirmationTarget = target.path("flight/" + flightConfirmationId);
			Response flightConfirmationResponse = flightConfirmationTarget.request().accept(MediaType.TEXT_PLAIN)
					.delete();

			if (flightConfirmationResponse.getStatus() == 200) {
				return true;
			}
		}

		throw new Exception("Rollbacking flight confirmation not possible!");
	}

	private static boolean rollbackHotelConfirmation(WebTarget target, String hotelConfirmationUrl) throws Exception {
		int retryCounter = 0;

		while (retryCounter <= MAX_RETRIES) {
			retryCounter++;

			String hotelConfirmationId = hotelConfirmationUrl.substring(hotelConfirmationUrl.lastIndexOf("/") + 1);
			WebTarget hotelConfirmationTarget = target.path("hotel/" + hotelConfirmationId);
			Response hotelConfirmationResponse = hotelConfirmationTarget.request().accept(MediaType.TEXT_PLAIN)
					.delete();

			if (hotelConfirmationResponse.getStatus() == 200) {
				return true;
			}
		}

		throw new Exception("Rollbacking hotel confirmation not possible!");
	}
}
