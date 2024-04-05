import { useState } from 'react';
import { View, Text, TextInput, Image, TouchableOpacity, ScrollView, Linking } from "react-native";
import styles from './authentication.style';
import AppButton from '../../components/AppButton';
import GetAppWrapper from '../../components/GetAppWrapper';
import MiddleAccountWrapper from '../../components/MiddleAccountWrapper';
import UrlWrapper from '../../components/UrlWrapper';
import AppText from '../../components/AppText.jsx';

const RegisterScreen = ({ navigation }) => {
    const [mobileOrEmail, setMobileOrEmail] = useState("")
    const [name, setName] = useState("")
    const [userName, setUserName] = useState("")
    const [password, setPassword] = useState("")

    return (
        <ScrollView >
            <View style={styles.parent}>
                <View style={styles.container}>
                    <Image
                        source={require("../../../assets/images/instagramText.png")}
                    />
                    <AppText
                        style={{ color: '#73737C', fontSize: 20, textAlign: 'center', paddingHorizontal: 10, fontWeight: '500', marginBottom: 10 }} >Sign up to see photos and videos from your friends.</AppText>
                    <TextInput
                        style={styles.inputTextStyle}
                        value={mobileOrEmail}
                        onChangeText={setMobileOrEmail}
                        placeholder="Mobile Number or Email"
                    />
                    <TextInput
                        style={styles.inputTextStyle}
                        value={name}
                        onChangeText={setName}
                        placeholder="Full Name"
                    />
                    <TextInput
                        style={styles.inputTextStyle}
                        value={userName}
                        onChangeText={setUserName}
                        placeholder="Username"
                    />
                    <TextInput
                        style={styles.inputTextStyle}
                        value={password}
                        onChangeText={setPassword}
                        placeholder="Password"
                    />

                    <UrlWrapper
                        text1={"People who use our service may have uploaded your contact information to Instagram. "}
                        clickableText1={"Learn More"}
                        onPress1={() => {
                            Linking.openURL("https://www.facebook.com/help/instagram/261704639352628");
                        }}
                    />
                    <UrlWrapper
                        text1={"By signing up, you agree to our "}
                        clickableText1={"Terms, "}
                        onPress1={() => {
                            Linking.openURL("https://help.instagram.com/581066165581870/?locale=en_US");
                        }}
                        clickableText2={"Privacy Policy "}
                        onPress2={() => {
                            Linking.openURL("https://www.facebook.com/privacy/policy");
                        }}
                        clickableText3={"Cookies Policy."}
                        onPress3={() => {
                            Linking.openURL("https://help.instagram.com/1896641480634370/");
                        }}
                    />
                    <AppButton
                        buttonName={"Sign up"}
                        onPress={() => {
                            navigation.navigate("BirthdayScreen")
                        }}
                    />
                </View>
                <MiddleAccountWrapper
                    name1={"Have an account?"}
                    name2={"Log in"}
                    onPress={() => navigation.goBack()}
                />
                <GetAppWrapper />
            </View>
        </ScrollView>
    )
};


export default RegisterScreen;