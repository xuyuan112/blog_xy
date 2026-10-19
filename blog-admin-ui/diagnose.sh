# 把以下输出贴给我
which node; node -v
ls -la /www/server/nodejs/ 2>/dev/null | head -20
ls -la /www/server/nodejs/*/bin/ 2>/dev/null | head -30
echo "---PATH---"; echo "$PATH"
which npm; npm -v
which npx
