module.exports = {
    entry: './app.js',
    output: {
        path: __dirname + 'app/',
        filename: 'bundle.js',
    },
    module: {
        loaders: [
            {
                test: /\.js?$/,
                exclude: /(node-module|bower-component)/,
                loader: 'babel',
            },
        ]
    },
    resolveLoader: {
        moduleExtensions: ['-loader']
    },
};