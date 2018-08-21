import { Directive, EventEmitter, HostListener, Input, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';
import 'rxjs/add/operator/timeout';
import 'rxjs/add/operator/first';

@Directive( {
    selector:  '[jr2FormValidator]'
} )
export class FormValidationDirective {

    @Input()
    public formGroup: FormGroup;

    @Output()
    public validSubmit = new EventEmitter<any>();

    @HostListener('submit')
    public onSubmit() {
        this.markAsTouchedAndDirty(this.formGroup);
        if (this.formGroup.valid) {
            this.validSubmit.emit(this.formGroup.value);
        }
        if (this.formGroup.pending) {
            this.formGroup.statusChanges
                .timeout(1000)
                .subscribe(() => {
                        if (this.formGroup.valid) {
                            this.validSubmit.emit(this.formGroup.value);
                        }
                    }
                    , () => {
                        console.warn('timeout');
                    });
        }
    }

    private markAsTouchedAndDirty(formGroup: FormGroup) {
        Object.keys(formGroup.controls)
            .forEach((key) => {
                const control = formGroup.controls[key];
                if (control instanceof FormGroup) {
                    this.markAsTouchedAndDirty(control as FormGroup);
                } else if (control.enabled) {
                    control.markAsDirty();
                    control.markAsTouched();
                    control.updateValueAndValidity();
                }
            });
    }

}
