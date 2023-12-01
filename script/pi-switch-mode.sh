#!/bin/bash

# Root privileges check
if [[ $EUID -ne 0 ]] ; then
  echo "Please run as root"
  exit
fi

# Location of the kernel boot cmdline parameters
KERNEL_CMDLINE="/boot/cmdline.txt"
OTG_MODE="modules-load=dwc2,g_ether"

# Location of Raspberry Pi boot config
BOOT_CONFIG="/boot/config.txt"
OVERLAY_DWC2="dtoverlay=dwc2"

echo
echo "Available modes:"
echo "1) OTG - allow the Pi to be detected as a USB device."
echo "2) Host - allow connect different devices to the Pi"
read -rp "Choose a mode: " mode
echo

if [[ $mode -eq 1 ]] ; then
  echo "Activating OTG mode..."
  # Create backup and remove any old module load setting, and add the new module load setting
  sed -i.backup \
      -e "s/[[:space:]]*$OTG_MODE\([[:space:]]*\)/\1/g" \
      -e "s/rootwait[[:space:]]*\([^[:space:]]*\)/rootwait $OTG_MODE \1/g" \
      "$KERNEL_CMDLINE"

  # Create backup and remove any old dwc2 overlay setting
  sed -i.backup \
      -e "/^[[:space:]]*$OVERLAY_DWC2.*$/ d" \
      "$BOOT_CONFIG"

  if $(grep -q '^otg_mode' $BOOT_CONFIG) ; then
    echo "Found 'otg_mode' setting. Commenting..."
    sed -i \
        -e "s/^otg_mode/#otg_mode/g" \
        "$BOOT_CONFIG"
    echo "Done"
  fi

  # Add the dwc2 dtoverlay setting
  echo "$OVERLAY_DWC2" >> "$BOOT_CONFIG"
elif [[ $mode -eq 2 ]] ; then
  echo "Activating HOST mode..."
  # Create backup and remove any old module load setting
  sed -i.backup \
      -e "s/[[:space:]]*$OTG_MODE\([[:space:]]*\)/\1/g" \
      "$KERNEL_CMDLINE"

  # Create backup and remove any old dwc2 overlay setting
  sed -i.backup \
      -e "/^[[:space:]]*$OVERLAY_DWC2.*$/ d" \
      "$BOOT_CONFIG"

  if $(grep -q '^#\+otg_mode' $BOOT_CONFIG) ; then
    echo "Found commented 'otg_mode' setting. Uncommenting..."
    sed -i \
        -e "s/^#\+otg_mode/otg_mode/g" \
        "$BOOT_CONFIG"
    echo "Done"
  fi
else
  echo "Incorrect mode!"
  exit
fi