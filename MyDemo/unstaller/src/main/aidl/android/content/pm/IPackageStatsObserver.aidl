
// IPackageStatsObserver.aidl
package android.content.pm;

// Declare any non-default types here with import statements

//interface IPackageStatsObserver {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
//    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
//            double aDouble, String aString);


            /**
             * API for package data change related callbacks from the Package Manager.
             * Some usage scenarios include deletion of cache directory, generate
             * statistics related to code, data, cache usage(TODO)
             * {@hide}
             */
  interface IPackageStatsObserver {

      void onGetStatsCompleted(in PackageStats pStats, boolean succeeded);
 }

