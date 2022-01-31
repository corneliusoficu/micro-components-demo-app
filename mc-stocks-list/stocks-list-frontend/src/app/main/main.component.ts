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
      },
      (error) => {                              //error() callback
        console.error('Request failed with error')
        console.log(error);
      },
      () => {                                   //complete() callback
        console.error('Request completed')      //This is actually not needed 
      })
  }

}