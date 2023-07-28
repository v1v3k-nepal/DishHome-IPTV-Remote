
var HexCodesCollection = {

    Power: "80BF3BC4",
    LiveTv: "80BF8B74",
    Movies: "80BFB14E",
    Account: "80BFC33C",
    Apps: "80BFF807",
    Youtube: "80BF7887",
    Netflix: "80BFF20D",
    Player: "80BFA05F",
    Refresh: "80BF8976",
    Rewind: "80BFB34C",
    Pause: "80BF21DE",
    Stop: "80BF6996",
    Forward: "80BF0BF4",

    Mouse: "80BF19E6",
    Return: "80BFA35C",

    Home: "80BF5BA4",
    Menu: "80BFF30C",

    VolumeUp: "80BF01FE",
    VolumeDown: "80BF817E",
    Settings: "80BF41BE",
    Mute: "80BF11EE",
    ChannelDown: "80BF619E",
    ChannelUp: "80BFA15E",

    NavLeft: "80BF9966",
    NavRight: "80BF837C",
    NavUp: "80BF53AC",
    NavDown: "80BF4BB4",
    Ok: "80BF738C",

    One: "80BF49B6",
    Two: "80BFC936",
    Three: "80BF33CC",
    Four: "80BF718E",
    Five: "80BFF10E",
    Six: "80BF13EC",
    Seven: "80BF51AE",
    Eight: "80BFD12E",
    Nine: "80BF23DC",
    Zero: "80BFE11E",
    Delete: "80BF39C6",
    CaseShift: "80BFA956"
};


function buttonClick(buttonId) {
    let buttonHexCode = HexCodesCollection[buttonId];
    let buttonPulsePattern = hexToPulsePattern(buttonHexCode);
      Android.HandleButtonClick(buttonPulsePattern, vibrationChoice);

    let focusedButton = document.getElementById(buttonId);
    focusedButton.addEventListener("animationend", () => focusedButton.blur());
}


//const HEX_CODE = "80BF11EE";

function hexToPulsePattern(hexCode) {
    const binaryCode = (parseInt(hexCode, 16).toString(2)).padStart(hexCode.length * 4, '0');
    const pulsePattern = [];

    // Header
    pulsePattern.push(9000, 4500);

    // Data bits
    for (const bit of binaryCode) {
        if (bit === "0") {
        pulsePattern.push(560, 560);
        } else {
        pulsePattern.push(560, 1690);
        }
    }

    // Trailing bit
    pulsePattern.push(560);

    return pulsePattern;
}

//const pulsePattern = hexToPulsePattern(HEX_CODE);
//console.log(pulsePattern);

let vibrationChoice = true;

function toggleVibrationChoice(){
    vibrationChoice = !vibrationChoice;

    const button = document.getElementById("vibrate-button");

    button.classList.remove("vibrate-on", "vibrate-off");

    if (vibrationChoice) {
        button.classList.add("vibrate-on");
        Android.showToast("Vibration Turned On");
      }
      
    else{
        button.classList.add("vibrate-off");
        Android.showToast("Vibration Turned Off");
    }

    Android.vibrateDevice();
}
