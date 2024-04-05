import { View, Text, Image, TouchableNativeFeedback, Linking } from "react-native";
import AppText from "./AppText";

export default GetAppWrapper = () => {
    return (
        <View>
            <View style={{
                alignItems: 'center',
                justifyContent: 'center',
                marginVertical: 10
            }}>
                <AppText style={{ fontSize: 14 }}>Get the app</AppText>
            </View>
            <View style={{ flexDirection: 'row', marginHorizontal: 50, justifyContent: 'center' }}>
                <TouchableNativeFeedback onPress={() => { Linking.openURL("https://play.google.com/store/apps/details?id=com.instagram.android&referrer=utm_source%3Dinstagramweb%26utm_campaign%3DloginPage%26ig_mid%3D1BFBA083-07B8-4742-9106-8EF0ABD9AE32%26utm_content%3Dlo%26utm_medium%3Dbadge"); }}>
                    <Image
                        style={{ height: 40, width: 120, marginHorizontal: 5 }} resizeMode='stretch'
                        source={{ uri: 'https://static.cdninstagram.com/rsrc.php/v3/yz/r/c5Rp7Ym-Klz.png' }} />
                </TouchableNativeFeedback>
                <TouchableNativeFeedback onPress={() => { Linking.openURL("https://apps.microsoft.com/store/detail/instagram/9NBLGGH5L9XT"); }}>
                    <Image
                        style={{ height: 40, width: 120, marginHorizontal: 5 }} resizeMode={"stretch"}
                        source={{ uri: 'https://static.cdninstagram.com/rsrc.php/v3/yu/r/EHY6QnZYdNX.png' }} />
                </TouchableNativeFeedback>
            </View>
        </View>);
}