import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModuloForm } from './modulo-form';

describe('ModuloForm', () => {
  let component: ModuloForm;
  let fixture: ComponentFixture<ModuloForm>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModuloForm],
    }).compileComponents();

    fixture = TestBed.createComponent(ModuloForm);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
