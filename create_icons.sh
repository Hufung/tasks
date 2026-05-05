#!/bin/bash

# Create simple placeholder PNG icons using ImageMagick (if available) or just create empty files
# For a real app, you would use proper icon assets

SIZES=("mdpi:48" "hdpi:72" "xhdpi:96" "xxhdpi:144" "xxxhdpi:192")

for size_spec in "${SIZES[@]}"; do
    IFS=':' read -r density pixels <<< "$size_spec"
    dir="app/src/main/res/mipmap-${density}"
    
    # Create a simple colored square as placeholder
    # In production, replace these with actual designed icons
    if command -v convert &> /dev/null; then
        convert -size ${pixels}x${pixels} xc:#6650a4 -fill white -gravity center \
                -pointsize $((pixels/3)) -annotate +0+0 "T" \
                "${dir}/ic_launcher.png"
        cp "${dir}/ic_launcher.png" "${dir}/ic_launcher_round.png"
    else
        # If ImageMagick not available, create empty placeholder files
        touch "${dir}/ic_launcher.png"
        touch "${dir}/ic_launcher_round.png"
    fi
done

echo "Icon placeholders created. Replace with actual designed icons for production."
