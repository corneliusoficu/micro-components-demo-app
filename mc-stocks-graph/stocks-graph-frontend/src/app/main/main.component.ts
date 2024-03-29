import { Component, OnInit, ViewEncapsulation, HostListener } from '@angular/core';
import * as CanvasJS from './canvasjs.min';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class MainComponent implements OnInit {

  constructor() { }

  chosenTicker: String = '';

  ngOnInit() {
    // addEventListener('stocks_list_item_clicked', this.onListItemClicked);
  }

  addDays = function(days) {
    var date = new Date(this.valueOf());
    date.setDate(date.getDate() + days);
    return date;
  }

  getDates(startDate, stopDate): Date[] {
      var dateArray = new Array();
      var currentDate = startDate;

      while (currentDate <= stopDate) {
          dateArray.push(new Date (currentDate));
          currentDate.setDate(currentDate.getDate() + 1);
      }
      return dateArray;
  }

  renderGraphForTicker(ticker:string, name:string): void {
    if(this.chosenTicker == ticker) {
      return;
    }

    this.chosenTicker = ticker;

    let currentDate = new Date();
    let oneYearAgoDate = new Date(new Date().setFullYear(currentDate.getFullYear()-1))
    let dates = this.getDates(oneYearAgoDate, currentDate)
  
    let dataPoints = [];
    let y = 0;		
  
  
    for ( var i = 0; i < dates.length; i++ ) {		  
      y += Math.round(5 + Math.random() * (-5 - 5));
      // let day = new Intl.DateTimeFormat('en-US', { day: 'long'}).format(dates[i])
      let month = new Intl.DateTimeFormat('en-US', { month: 'short'}).format(dates[i])
      let date = `${dates[i].getDate()}/${month}`	
      dataPoints.push({ y: y,label: date});
    }
    let chart = new CanvasJS.Chart("chartContainer", {
      zoomEnabled: true,
      animationEnabled: true,
      exportEnabled: true,
      title: {
        text: `${name} stock`
      },
      subtitles:[{
        text: "1 year stock graph"
      }],
      data: [
      {
        type: "line",                
        dataPoints: dataPoints
      }]
    });
    
      
    chart.render();
  }

  @HostListener('window:stocks_list_item_clicked', ['$event'])
  onListItemClicked(event: any) {
    var stockTicker = event.detail.ticker;
    var stockName = event.detail.name;
    console.log(`Rendering graph for ${stockTicker} - ${stockName}`)

    this.renderGraphForTicker(stockTicker, stockName);
  }
}