<!DOCTYPE html>
<html lang="en">
<head>
    <title>Telegram Sign In</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 dark:bg-gray-900 flex items-center justify-center min-h-screen">
<div class="bg-gray-200 dark:bg-gray-800 p-6 rounded-lg shadow-md w-full max-w-md">
    <div class="flex justify-center mb-4">
        <img
                src="https://upload.wikimedia.org/wikipedia/commons/8/82/Telegram_logo.svg"
                alt="Telegram Logo" class="h-10">
    </div>
    <h2 class="flex justify-center dark:invert">Please login with Telegram below</h2>
    <div class="flex justify-center">
        <script async src="https://telegram.org/js/telegram-widget.js?22"
                data-telegram-login="${config.TELEGRAM_BOT}" data-size="large"
                data-auth-url="${config.URL}/telegram/callback"></script>
    </div>
</div>
</body>
</html>