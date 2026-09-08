import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BookingApiService, BookingCreateRequest } from './booking-api.service';
import { environment } from '../../../environments/environment';

describe('BookingApiService', () => {
  let service: BookingApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(BookingApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  it('fetches reserved seats', () => {
    const showtimeId = 123;
    const mock = ['A1','A2'];
    service.getReservedSeats(showtimeId).subscribe(list => {
      expect(list).toEqual(mock);
    });
    const req = httpMock.expectOne(`${environment.apiBase}/bookings/showtimes/${showtimeId}/reserved-seats`);
    expect(req.request.method).toBe('GET');
    req.flush(mock);
  });

  it('returns conflict object on 409', () => {
    const payload: BookingCreateRequest = { userId:1,movieId:2,theatreId:3,showtimeId:4,totalAmount:500,paymentId:0,bookingStatus:'CONFIRMED',seatCount:1,seatNumbers:['A3'] };
    service.createBooking(payload).subscribe({
      next: () => fail('should error'),
      error: err => {
        expect(err.conflict).toBeTrue();
      }
    });
    const req = httpMock.expectOne(`${environment.apiBase}/bookings/create`);
    req.flush({ error: 'One or more seats just got booked. Please retry with different seats.' }, { status:409, statusText:'Conflict' });
  });
});
