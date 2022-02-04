import { TestBed } from '@angular/core/testing';

import { StockActionsService } from './stock-actions.service';

describe('StockActionsService', () => {
  let service: StockActionsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(StockActionsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
