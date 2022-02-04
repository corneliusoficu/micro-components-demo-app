import { Component, HostListener, OnInit, ViewEncapsulation } from '@angular/core';
import { StocksService } from '../stocks.service';
import { UserStock } from '../models/UserStock';
import { BehaviorSubject, Observable, Subject } from 'rxjs';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class MainComponent implements OnInit {
  portfolioStocks: UserStock[] = []
  totalOwnedValue = 0;
  reloader$ = new Subject();
  constructor(private stocksService: StocksService) { }

  getUserStocks() {
    var currentUser = localStorage.getItem('currentUser')

    if(!currentUser) {
      console.log("No user in local storage!")
      return;
    }

    var currentUserJson = JSON.parse(currentUser);

    this.stocksService.getUserStocks(currentUserJson["token"])
      .subscribe(
        (response)=>{
          console.log("Got Response from backend")
          this.portfolioStocks = response
          this.totalOwnedValue = this.portfolioStocks.map(stock => stock.shareValue * stock.shares).reduce((previousValue, currentValue) => previousValue + currentValue, 0)
        },
        (error) => {                              //error() callback
          console.error('Request failed with error')
          console.error(error);
        });
  }

  ngOnInit(): void {
    this.getUserStocks();
  }

  onListitemClick(stockItem) {
    console.log("List Item clicked!")
    console.log(stockItem);
    const event = new CustomEvent('stocks_list_item_clicked', {detail: stockItem});
    dispatchEvent(event);
  }

  @HostListener('window:stocks_list_refresh_list', ['$event'])
  onListItemClicked(event: any) {
    console.log("Refresing list of stocks for user")
    this.portfolioStocks = [];
    this.totalOwnedValue = 0;
    this.getUserStocks();
  }

}