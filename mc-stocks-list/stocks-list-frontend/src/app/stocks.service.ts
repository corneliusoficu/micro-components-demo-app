import { Injectable } from '@angular/core';
import { HttpClientModule, HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserStock } from './models/UserStock';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class StocksService {

  userStocksUrl = "/owned-stocks"

  constructor(private http: HttpClient) { }

  getUserStocks(bearerToken): Observable<UserStock[]> {
    console.log("Getting user owned stocks")
    return this.http.get<UserStock[]>(`${environment.stocksListBaseUrl}${this.userStocksUrl}`, {headers: {"Authorization": `Bearer ${bearerToken}`}})
  }
}
