/**
 * com.williamrijksen.onesignal
 *
 * Created by Your Name
 * Copyright (c) 2016 Your Company. All rights reserved.
 */

#import "TiModule.h"
#import <OneSignal/OneSignal.h>

@interface ComWilliamrijksenOnesignalModule : TiModule {}

typedef void(^TagsResultHandler)(NSDictionary*, NSError*);

- (void)sendTag:(id)args;
- (void)deleteTag:(id)args;
- (void)getTags:(id)value;
- (void)setLogLevel:(id)args;
- (void)idsAvailable:(id)args;
- (void)setSubscription:(id)value;
- (void)postNotification:(id)arguments;

@end
