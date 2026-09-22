#!/usr/bin/env bash
set -euo pipefail
rm -rf build patch-classes
mkdir -p build patch-classes/com/bx/magicSmp/patch
cp baseline/MagicSMP.jar build/MagicSMP.jar
cp guard/target/classes/com/bx/magicSmp/patch/SellGuiProtectionListener.class patch-classes/com/bx/magicSmp/patch/
# Remove the old compiler-generated switch helper from v2/v3; v4 no longer uses it.
zip -d build/MagicSMP.jar 'com/bx/magicSmp/patch/SellGuiProtectionListener$1.class' >/dev/null 2>&1 || true
(cd patch-classes && jar uf ../build/MagicSMP.jar com/bx/magicSmp/patch/SellGuiProtectionListener.class)
echo "Built build/MagicSMP.jar"
