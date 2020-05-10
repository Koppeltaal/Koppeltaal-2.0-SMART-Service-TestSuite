const form_cookie = {
  saveCookie: function () {
    $('form').each(function () {
      const x = $(this).attr('name');
      const str = $(this).serialize();
      $.cookie(x, str, {expires: 2});
    });
  },
  restoreCookie: function () {
    let rv = false;
    $('form').each(function () {
      const x = $(this).attr('name');
      const str = $.cookie(x) || '';
      const data = str.split('&');
      $(data).each(function () {
        const a = this.split('=');
        if (a.length > 1) {
          const value = decodeURIComponent(a[1]);
          $(':input[name="' + a[0] + '"]').val(value);
          $(':input[name="' + a[0] + '"][type=checkbox]').prop('checked', true);
          rv = true;
        }
      })
    });
    return rv;
  }
}

const form_utils = {

  serializeForm: function (selector) {
    let rv = {}
    const array = $(selector).serializeArray();
    for (let i in array) {
      if (array.hasOwnProperty(i)) {
        let name = array[i]['name'];
        rv[name] = array[i]['value'];
      }
    }
    return rv;
  }

}

