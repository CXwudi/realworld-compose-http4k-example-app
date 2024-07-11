// from https://cashapp.github.io/sqldelight/2.0.2/js_sqlite/sqljs_worker/
// and https://github.com/TarasovVP/Android-Architecure-Templates/tree/kmp_implementation/webApp/webpack.config.d

// this is needed otherwise the sqldelight won't properly initialized
// similar issue see: https://github.com/joreilly/PeopleInSpace/issues/49
config.resolve = {
    fallback: {
        fs: false,
        path: false,
        crypto: false,
    },
};

// the error shows "expected magic word 00 61 73 6d, found 3c 21 44 4f", but it really means the wasm file is missing
// see https://github.com/WebAssembly/spec/issues/1031#issuecomment-498314525
// In SQLDelight, the sql-wasm.wasm is missing, like https://github.com/jepiqueau/react-sqlite-app-starter/issues/16#issuecomment-1541359073
// so we add the wasm file manually
const CopyWebpackPlugin = require('copy-webpack-plugin');
config.plugins.push(
    new CopyWebpackPlugin({
        patterns: [
            '../../node_modules/sql.js/dist/sql-wasm.wasm'
        ]
    })
);