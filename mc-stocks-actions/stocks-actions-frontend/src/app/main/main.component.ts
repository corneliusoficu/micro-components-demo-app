import { Component, OnInit, ViewEncapsulation, Inject } from '@angular/core';
import { StockActionsService } from '../stock-actions.service';
import { StockOrder } from '../model/StockOrder';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

export class DialogData {
  title: string;
  message: string;
}

@Component({
  selector: 'stocks-dialog',
  templateUrl: './stock-actions-dialog.html'
})
export class StocksDialog {
  constructor(public dialogRef: MatDialogRef<StocksDialog>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) {}

    onNoClick(): void {
      this.dialogRef.close();
    }

}

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.css'],
  encapsulation: ViewEncapsulation.None
})
export class MainComponent implements OnInit {
  ticker: string = ""

  autoTicks = false;
  disabled = false;
  invert = false;
  max = 1000;
  min = 0;
  showTicks = false;
  step = 1;
  thumbLabel = false;
  value = 0;
  vertical = false;
  tickInterval = 1;

  constructor(private stockActionsService: StockActionsService, public dialog: MatDialog) { }

  ngOnInit(): void {
  }

  getSliderTickInterval(): number | 'auto' {
    if (this.showTicks) {
      return this.autoTicks ? 'auto' : this.tickInterval;
    }

    return 0;
  }

  onSharesAction(sharesAction) {
    var stockOrder: StockOrder = new StockOrder();
    stockOrder.orderType = sharesAction;
    stockOrder.shares = this.value;
    stockOrder.ticker = this.ticker;

    var currentUser = localStorage.getItem('currentUser')

    if(!currentUser) {
      console.log("No user in local storage!")
      return;
    }

    var currentUserJson = JSON.parse(currentUser);

    this.stockActionsService.executeStockOrder(currentUserJson["token"], stockOrder).subscribe(
      (res) => {
        alert("Executed")
      },
      (error) => {
        console.error("Error occured!", error.message)
        this.openDialog("Error occured!", error.message);
      });
  }

  openDialog(title, message) {
    this.dialog.open(StocksDialog, {data: {
      title: title,
      message: message
    }});
  }

}