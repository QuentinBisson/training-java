//On load
$(function () {
    const formGroupSuccess = 'has-success';
    const formGroupError = 'has-danger';
    const formControlSuccess = 'form-control-success';
    const formControlError = 'form-control-danger';
    const formControlFeedbackClass = '.form-control-error';

    // Create date matcher from localized date format
    const dateFormat = i18nFromJava.date.format
        .replace("dd", "(0[1-9]|1[0-2])")
        .replace("MM", "(0[1-9]|[12][0-9]|3[01])")
        .replace("yyyy", "(\\d{4})");
    const dateRegex = new RegExp('^' + dateFormat + '$');


    function cleanState($element) {
        $element.parent().removeClass(formGroupSuccess + ' ' + formGroupError);
        $element.removeClass(formControlSuccess + ' ' + formControlError);
    }

    const addError = function ($element) {
        cleanState($element);
        $element.parent().addClass(formGroupError);
        $element.addClass(formControlError);
    };

    const addSuccess = function ($element) {
        cleanState($element);
        $element.parent().addClass(formGroupSuccess);
        $element.addClass(formControlSuccess);
    };

    const isValidDate = function (str) {
        // Match iso date
        return str.match(dateRegex);
    };

    function validateDate(identifier, errorMessage) {
        var $date = $(identifier);
        if (!$date.val() || isValidDate($date.val())) {
            addSuccess($date);
            $date.siblings(formControlFeedbackClass).text('');
        } else {
            addError($date);
            $date.siblings(formControlFeedbackClass).text(errorMessage);
            return false;
        }
        return true;
    }

    $("#computer-form").on('submit', function () {
        var valid = true;

        var $computerName = $('#computerName');
        if ($computerName.val() && $computerName.val().length >= 3) {
            addSuccess($computerName);
            $computerName.siblings(formControlFeedbackClass).text('');
        } else {
            addError($computerName);
            $computerName.siblings(formControlFeedbackClass).text(i18nFromJava.computers.constraints.name.toosmall);
            valid = false;
        }

        valid = validateDate('#introduced', i18nFromJava.computers.constraints.introduced.invalid) && valid;
        valid = validateDate('#discontinued', i18nFromJava.computers.constraints.discontinued.invalid) && valid;

        addSuccess($('#companyId'));

        return valid;
    });
});