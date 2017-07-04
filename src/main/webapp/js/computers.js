//On load
$(function () {
    const formGroupSuccess = 'has-success';
    const formGroupError = 'has-danger';
    const formControlSuccess = 'form-control-success';
    const formControlError = 'form-control-danger';
    const formControlFeedbackClass = '.form-control-error';

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
        return str.match(/^(\d{1,2})\/(\d{1,2})\/(\d{4})$/);
    };

    function validateDate(identifier) {
        var $date = $(identifier);
        if (!$date.val() || isValidDate($date.val())) {
            addSuccess($date);
            $date.siblings(formControlFeedbackClass).text('');
        } else {
            addError($date);
            $date.siblings(formControlFeedbackClass).text('Invalid timestamp !');
            return false;
        }
        return true;
    }

    $("#computer-form").on('submit', function () {
        var valid = true;

        var $computerName = $('#computerName');
        if ($computerName.val()) {
            addSuccess($computerName);
            $computerName.siblings(formControlFeedbackClass).text('');
        } else {
            addError($computerName);
            $computerName.siblings(formControlFeedbackClass).text('Name must be filled !');
            valid = false;
        }

        valid = validateDate('#introduced') && valid;
        valid = validateDate('#discontinued') && valid;

        addSuccess($('#companyId'));

        return valid;
    });

});