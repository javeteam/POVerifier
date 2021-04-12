const notifications = new PageNotifications();

function displayError(xhr) {
    const jsonStr = JSON.parse(xhr.responseText);
    notifications.push(jsonStr.error, jsonStr.message, 'error', false);
}

$(document).ready(function () {
    $('form').trigger('reset');
    $('select').select2({
        width: '200px',
        theme: "default select2-dark"
    });
});

let csvOK = false;
let xtrfOK = false;

// For stupid chrome which don't fire 'change' event on select file with the same name
$(document).on('click', 'input[name="csvFile"]', function () {
    $(this).closest('form').trigger('reset');
})

$(document).on('change', 'input[name="csvFile"]', function () {
    const form = $(this).closest('form');
    if(form.get(0).checkValidity()){
        ajax_json(form)
            .done(function (response) {
                const rowsCount = parseInt(response.rowsCount);
                $('.js_csv_info .value').text(rowsCount);
                if(rowsCount > 0){
                    $('.js_csv_info').removeClass('empty');
                    csvOK = true;
                    compare();
                } else{
                    $('.js_csv_info').addClass('empty');
                    $('.document').empty();
                }
            }).fail(function (xhr) {
                csvOK = false;
                form.trigger("reset");
                $('.js_csv_info').addClass('empty');
                $('.js_csv_info .value').text(0);
                $('.document').empty();
                displayError(xhr);
            })
    } else{
        form.trigger("reset");
        csvOK = false;
    }
});

$(document).on('change', '.xtrf_section input, .xtrf_section select', function () {
    const form = $(this).closest('form');
    if(form.get(0).checkValidity()){
        ajax_json(form)
            .done(function (response) {
                const rowsCount = parseInt(response.rowsCount);
                $('.js_xtrf_info .value').text(rowsCount);
                if(rowsCount > 0){
                    $('.js_xtrf_info').removeClass('empty');
                    xtrfOK = true;
                    compare();
                }
                else{
                    $('.js_xtrf_info').addClass('empty');
                    $('.document').empty();
                }
            }).fail(function (xhr) {
                xtrfOK = false;
                $('.js_xtrf_info').addClass('empty');
                $('.js_xtrf_info .value').text(0);
                $('.document').empty();
                displayError(xhr);
            })
    }
});

function ajax_json(form) {
    return $.ajax({
        url: form.attr('action'),
        method: "POST",
        data: new FormData(form.get(0)),
        processData: false,
        contentType: false,
        data_type: 'json'
    })
}

function compare(){
    if(!xtrfOK || !csvOK) return;
    $.ajax({
        url: $('.menu').data('url'),
        method: "POST",
        data: {},
        processData: false,
        contentType: false,
        cache: false
    }).done(function (response) {
        const doc = new DOMParser().parseFromString(response, 'text/html');
        $('.document').replaceWith($(doc).find('.document'));
    }).fail(function (xhr) {
        displayError(xhr);
    });
}

$(document).on('focus', '.login_form input', function () {
    $(this).closest('form').find('input').removeAttr('readonly');
})


$(document).on('click', '.login_form .button', function () {
    $(this).closest('form').trigger('submit');
})

$(document).on('keypress', '.login_form input', function (e) {
    const form = $(this).closest('form');
    if(e.keyCode === 13 && form.get(0).checkValidity()) form.trigger('submit');
})

