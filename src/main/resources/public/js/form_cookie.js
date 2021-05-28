/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright Headease B.V. (c) 2020.
 */

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
    $('form#smart-testsuite_form').each(function () {
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

