import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { StocksService } from '../stocks.service';
import { UserStock } from '../models/UserStock';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class MainComponent implements OnInit {
  portfolioStocks: UserStock[] = []
  totalOwnedValue = 0;
  constructor(private stocksService: StocksService) { }

  ngOnInit(): void {
    var currentUser = localStorage.getItem('currentUser')

    if(!currentUser) {
      console.log("No user in local storage!")
      return;
    }

    var currentUserJson = JSON.parse(currentUser);

    this.stocksService.getUserStocks(currentUserJson["token"]).subscribe(
      (response)=>{
        this.portfolioStocks = response
        this.totalOwnedValue = this.portfolioStocks.map(stock => stock.shareValue * stock.shares).reduce((previousValue, currentValue) => previousValue + currentValue, 0)
      },
      (error) => {                              //error() callback
        console.error('Request failed with error')
        console.error(error);
      })
  }

  onListitemClick(stockItem) {
    console.log("List Item clicked!")
    console.log(stockItem);
    const event = new CustomEvent('stocks_list_item_clicked', {detail: stockItem});
    dispatchEvent(event);
  }

}