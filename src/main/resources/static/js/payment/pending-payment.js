let tableIndex = 0;

function queryParams(params) {
    return {
        limit:  params.limit,
        offset: params.offset
    };
}

function responseHandler(res) {
    tableIndex = 0;
    return res;
}

function indexFormatter(value, row, index) {
    return ++tableIndex;
}

function amountFormatter(value) {
    return value
        ? '₹' + Number(value).toLocaleString('en-IN')
        : '—';
}