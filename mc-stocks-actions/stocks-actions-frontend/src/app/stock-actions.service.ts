import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { StockOrder } from './model/StockOrder';

@Injectable({
  providedIn: 'root'
})
export class StockActionsService {
  constructor(private http: HttpClient) { }

  executeStockOrder(stockOrder: StockOrder) {
    return this.http.post(`${environment.stockActionsBackend}/executeStockOrder`, stockOrder);
  }

  
}
